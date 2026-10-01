package ticket_booking_system.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ticket_booking_system.ENUM.BookingStatus;
import ticket_booking_system.ENUM.PassengerStatus;
import ticket_booking_system.ENUM.SeatPosition;
import ticket_booking_system.ENUM.SeatStatus;
import ticket_booking_system.dto.*;
import ticket_booking_system.entity.*;
import ticket_booking_system.exception.*;
import ticket_booking_system.repository.bookingPassengerRepository;
import ticket_booking_system.repository.bookingRepository;
import ticket_booking_system.repository.tripRepository;
import ticket_booking_system.repository.tripSeatRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class BookingService {

    @Autowired
    tripRepository tripRepository;

    @Autowired
    tripSeatRepository tripSeatRepository;

    @Autowired
    bookingRepository bookingRepository;

    @Autowired
    bookingPassengerRepository bookingPassengerRepository;

    @Transactional
    public BookingResponseDTO bookTickets(BookingRequestDTO bookingRequestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                user = (User) principal;
            } else throw new BookingException("Authenticated principal is not a valid User.");
        } else throw new BookingException("User is not authenticated.");

        Trip trip = tripRepository.findById(bookingRequestDTO.getTripId())
                .orElseThrow(() -> {
                    log.error("Trip Id {} not found", bookingRequestDTO.getTripId());
                    return new TripNotFoundException("TripId " + bookingRequestDTO.getTripId() + " not found");
                });

        if (bookingRequestDTO.getPassengers() == null || bookingRequestDTO.getPassengers().isEmpty()) {
            log.error("Passenger list is empty");
            throw new BookingException("Passenger list is empty");
        }

        if (trip.getAvailableSeats() < bookingRequestDTO.getPassengers().size()) {
            log.error("No Available Seats");
            throw new BookingException("No Available Seats");
        }

        Set<Long> SeatIds = new HashSet<>();
        for (PassengerDTO passenger : bookingRequestDTO.getPassengers()) {
            if (!SeatIds.add(passenger.getTripSeatId())) {
                log.error("Seat ID " + passenger.getTripSeatId() + " is duplicate");
                throw new BookingException("Seat ID " + passenger.getTripSeatId() + " is duplicate");
            }

        }

        Map<Long, TripSeat> tripSeatMap = new HashMap<>();
        for (PassengerDTO passenger : bookingRequestDTO.getPassengers()) {
            TripSeat tripSeat = tripSeatRepository.findById(passenger.getTripSeatId()).
                    orElseThrow(() -> {
                        log.error("Trip SeatId {} not found", passenger.getTripSeatId());
                        return new BookingException("TripSeatId     " + passenger.getTripSeatId() + " not found");
                    });
            tripSeatMap.put(tripSeat.getId(), tripSeat);
        }

        for (PassengerDTO passenger : bookingRequestDTO.getPassengers()) {
            Long SeatId = passenger.getTripSeatId();
            if (!tripSeatMap.get(SeatId).getTrip().getId().equals(trip.getId())) {
                log.error(tripSeatMap.get(SeatId).getId() + " this TripSeat is not belongs to " + trip.getId() + " this trip");
                throw new BookingException(tripSeatMap.get(SeatId).getId() + " this TripSeat is not belongs to " + trip.getId() + " this trip");
            }

            if (tripSeatMap.get(SeatId).getSeatStatus() != SeatStatus.AVAILABLE) {
                log.error("Selected seat is already booked. Please choose another seat.");
                throw new BookingException("Selected seat is already booked. Please choose another seat.");
            }
        }

        validateGenderBasedSeatAllocation(bookingRequestDTO, tripSeatMap);

        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setTrip(trip);
        booking.setUser(user);

        List<BookingPassenger> passengerList = new ArrayList<>();
        List<TripSeat> updatedSeats = new ArrayList<>();
        for (PassengerDTO passenger : bookingRequestDTO.getPassengers()) {
            Long seatId = passenger.getTripSeatId();
            TripSeat tripseat = tripSeatMap.get(seatId);
            BookingPassenger bookingPassenger = new BookingPassenger();
            bookingPassenger.setBooking(booking);
            bookingPassenger.setPassengerName(passenger.getPassengerName());
            bookingPassenger.setAge(passenger.getAge());
            bookingPassenger.setGender(passenger.getGender());
            bookingPassenger.setTripSeat(tripseat);
            bookingPassenger.setPassengerStatus(PassengerStatus.CONFIRMED);
            passengerList.add(bookingPassenger);
        }

        BigDecimal totalAmt = BigDecimal.ZERO;
        try {
            totalAmt = trip.getBus().getFare().multiply(new BigDecimal(passengerList.size()));
            log.info("Total payable amount: " + totalAmt);
            log.info("Payment is inprogess..... ");
            Thread.sleep(3000);
            log.info("Payment Completed Successfully, Have a safe journey!");
        } catch (InterruptedException ex) {
            throw new RuntimeException("Payment Failed");
        }

        for (PassengerDTO passenger : bookingRequestDTO.getPassengers()) {
            Long seatId = passenger.getTripSeatId();
            TripSeat tripseat = tripSeatMap.get(seatId);
            tripseat.setSeatStatus(SeatStatus.BOOKED);
            updatedSeats.add(tripseat);
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        bookingPassengerRepository.saveAll(passengerList);
        tripSeatRepository.saveAll(updatedSeats);
        trip.setAvailableSeats(trip.getAvailableSeats() - bookingRequestDTO.getPassengers().size());
        tripRepository.save(trip);

        BookingResponseDTO bookingResponse = new BookingResponseDTO();
        bookingResponse.setBookingId(booking.getId());
        bookingResponse.setBookingStatus(booking.getBookingStatus());
        bookingResponse.setTripId(trip.getId());
        bookingResponse.setTotalAmount(totalAmt);
        bookingResponse.setPassengersCount(passengerList.size());
        bookingResponse.setMessage("Booking has been Completed Successfully! Have a safe journey!");


        return bookingResponse;
    }

    public void validateGenderBasedSeatAllocation(BookingRequestDTO request, Map<Long, TripSeat> tripSeatMap) {

        List<TripSeat> tripSeats = tripSeatRepository.findByTripId(request.getTripId());

        for (PassengerDTO passenger : request.getPassengers()) {
            TripSeat CurrentSeat = tripSeatMap.get(passenger.getTripSeatId());
            SeatPosition seatPosition = CurrentSeat.getSeat().getSeatPosition();
            List<SeatPosition> adjacentPositions = new ArrayList<>();

            switch (seatPosition) {
                case LEFT_WINDOW -> adjacentPositions.add(SeatPosition.LEFT_MIDDLE);
                case LEFT_MIDDLE -> {
                    adjacentPositions.add(SeatPosition.LEFT_WINDOW);
                    adjacentPositions.add(SeatPosition.LEFT_AISLE);
                }
                case LEFT_AISLE -> adjacentPositions.add(SeatPosition.LEFT_MIDDLE);
                case RIGHT_WINDOW -> adjacentPositions.add(SeatPosition.RIGHT_MIDDLE);
                case RIGHT_MIDDLE -> {
                    adjacentPositions.add(SeatPosition.RIGHT_WINDOW);
                    adjacentPositions.add(SeatPosition.RIGHT_AISLE);
                }
                case RIGHT_AISLE -> adjacentPositions.add(SeatPosition.RIGHT_MIDDLE);
            }

            for (SeatPosition adjacentPosition : adjacentPositions) {
                for (TripSeat ts : tripSeats) {
                    if (ts.getSeat().getSeatRow() == CurrentSeat.getSeat().getSeatRow() &&
                            ts.getSeat().getSeatPosition() == adjacentPosition) {
                        if (ts.getSeatStatus() != SeatStatus.AVAILABLE) {
                            BookingPassenger bookingPassenger = bookingPassengerRepository.findByTripSeatId(ts.getId());
                            if (bookingPassenger != null && bookingPassenger.getGender() != passenger.getGender()) {
                                log.error("Adjacent Seat is booked by "
                                        + bookingPassenger.getGender() + " Kindly book another seat!");
                                throw new AdjacentSeatException("Adjacent Seat is booked by "
                                        + bookingPassenger.getGender() + " Kindly book another seat!");
                            }
                        }
                    }
                }

            }


        }

    }

    public List<BookedPassengerResponseDTO> getBookedPassengers(Long bookingId) {
        List<BookingPassenger> bookedPassengersList = bookingPassengerRepository.findByBookingId(bookingId);
        List<BookedPassengerResponseDTO> response = new ArrayList<>();

        for (BookingPassenger passenger : bookedPassengersList) {
            BookedPassengerResponseDTO passengerResponse = new BookedPassengerResponseDTO();
            passengerResponse.setId(passenger.getId());
            passengerResponse.setPassengerName(passenger.getPassengerName());
            passengerResponse.setAge(passenger.getAge());
            passengerResponse.setPassengerStatus(passenger.getPassengerStatus());
            passengerResponse.setGender(passenger.getGender());
            passengerResponse.setTripSeat_Id(passenger.getTripSeat().getId());
            passengerResponse.setSeatNumber(passenger.getTripSeat().getSeat().getSeatNumber());
            response.add(passengerResponse);
        }

        return response;
    }


    @Transactional
    public BookingCancellationResponseDTO fullBookingCancellation(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).
                orElseThrow(() -> new BookingNotFoundException(bookingId + " is not valid!"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                user = (User) principal;
                if (!user.getEmail().equals(booking.getUser().getEmail())) {
                    throw new InvalidCredentialsException("Invalid user is deleting!");
                }
            } else throw new InvalidCredentialsException("Authenticated principal is not a valid User.");
        } else throw new InvalidCredentialsException("User is not authenticated.");

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("This booking is already cancelled!");
        }

        List<BookingPassenger> bookedPassengersList = bookingPassengerRepository.findByBookingId(bookingId);

        if (bookedPassengersList.size() == 0) {
            throw new BookingException("No passengers are available under this booking!");
        }

        List<TripSeat> tripSeatList = new ArrayList<>();
        Trip trip = booking.getTrip();

        for (BookingPassenger bookedPassenger : bookedPassengersList) {
            TripSeat ts = bookedPassenger.getTripSeat();
            ts.setSeatStatus(SeatStatus.AVAILABLE);
            tripSeatList.add(ts);
            bookedPassenger.setPassengerStatus(PassengerStatus.CANCELLED);
        }

        Long updatedSeatCount = trip.getAvailableSeats() + bookedPassengersList.size();
        trip.setAvailableSeats(updatedSeatCount);
        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        tripSeatRepository.saveAll(tripSeatList);
        bookingPassengerRepository.saveAll(bookedPassengersList);
        tripRepository.save(trip);
        bookingRepository.save(booking);

        BookingCancellationResponseDTO bookingCancellationResponseDTO = new BookingCancellationResponseDTO();
        bookingCancellationResponseDTO.setBookingStatus(BookingStatus.CANCELLED);
        bookingCancellationResponseDTO.setPassengersCount((long) bookedPassengersList.size());
        bookingCancellationResponseDTO.setMessage("Your Tickets has been cancelled successfully!");
        bookingCancellationResponseDTO.setCancelledAt(booking.getCancelledAt());

        return bookingCancellationResponseDTO;

    }


    @Transactional
    public BookingCancellationResponseDTO partialCancellation(Long bookingId, PartialCancellationRequestDTO partialCancellationRequestDTO) {
        Booking booking = bookingRepository.findById(bookingId).
                orElseThrow(() -> new BookingNotFoundException("BookingId " + bookingId + " is not valid!"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                user = (User) principal;
                if (!user.getEmail().equals(booking.getUser().getEmail())) {
                    throw new InvalidCredentialsException("Invalid user is deleting!");
                }
            } else throw new InvalidCredentialsException("Authenticated principal is not a valid User.");
        } else throw new InvalidCredentialsException("User is not authenticated.");

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BookingException(bookingId + " This Booking is already in cancelled!");
        }

        List<BookingPassenger> bookingPassengerList = bookingPassengerRepository.findByBookingId(bookingId);

        Trip trip = booking.getTrip();

        List<TripSeat> tripSeatList = new ArrayList<>();

        Set<Long> passengersIds = partialCancellationRequestDTO.getPassengersIds();

        if (passengersIds == null || passengersIds.isEmpty()) {
            throw new BookingException("No passenger IDs were provided!");
        }

        Long updatedSeatsCount = 0L;
        boolean hasConfirmedPassenger = false;

        for (Long passengerId : passengersIds) {

            BookingPassenger bp = bookingPassengerList.stream().filter(x -> x.getId().equals(passengerId)).findFirst()
                    .orElseThrow(() -> new BookingException(passengerId + " passengerId is not valid!"));

            if (bp.getPassengerStatus() == PassengerStatus.CANCELLED) {
                throw new BookingException("Passenger ticket is already in cancelled!");
            }

            TripSeat ts = bp.getTripSeat();
            ts.setSeatStatus(SeatStatus.AVAILABLE);
            bp.setPassengerStatus(PassengerStatus.CANCELLED);
            updatedSeatsCount++;
            tripSeatList.add(ts);
        }

        trip.setAvailableSeats(trip.getAvailableSeats() + updatedSeatsCount);

        for (BookingPassenger bp : bookingPassengerList) {
            if (bp.getPassengerStatus() == PassengerStatus.CONFIRMED) {
                hasConfirmedPassenger = true;
                break;
            }
        }

        if (!hasConfirmedPassenger) {
            booking.setBookingStatus(BookingStatus.CANCELLED);
            booking.setCancelledAt(LocalDateTime.now());
        }

        bookingPassengerRepository.saveAll(bookingPassengerList);
        tripSeatRepository.saveAll(tripSeatList);
        tripRepository.save(trip);
        bookingRepository.save(booking);

        BookingCancellationResponseDTO bookingCancellationResponseDTO = new BookingCancellationResponseDTO();
        bookingCancellationResponseDTO.setBookingStatus(booking.getBookingStatus());
        bookingCancellationResponseDTO.setPassengersCount(updatedSeatsCount);
        bookingCancellationResponseDTO.setMessage("Your Tickets has been cancelled successfully!");
        bookingCancellationResponseDTO.setCancelledAt(booking.getCancelledAt());


        return bookingCancellationResponseDTO;

    }

    public List<BookingHistoryResponseDTO> bookingHistory() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                user = (User) principal;
            } else throw new InvalidCredentialsException("User is not authenticated.");
        } else throw new InvalidCredentialsException("User is not authenticated.");

        Long userId = user.getId();

        List<Booking> bookings = bookingRepository.findByUserId(userId);

        List<BookingHistoryResponseDTO> bookingResponses = new ArrayList<>();

        for (Booking booking : bookings) {
            for (BookingPassenger passenger : booking.getBookingPassengers()) {
                BookingHistoryResponseDTO bh = new BookingHistoryResponseDTO();
                bh.setUserId(userId);
                bh.setBusNumber(booking.getTrip().getBus().getBusNumber());
                bh.setBusName(booking.getTrip().getBus().getBusName());
                bh.setSource(booking.getTrip().getBus().getSource());
                bh.setDestination(booking.getTrip().getBus().getDestination());
                bh.setBookingStatus(booking.getBookingStatus());
                bh.setPassengerStatus(passenger.getPassengerStatus());
                bh.setPassengerName(passenger.getPassengerName());
                bh.setAge(passenger.getAge());
                bh.setBookingTime(booking.getBookingTime());
                bh.setJourneyDate(booking.getTrip().getJourneyDate());
                bh.setBookingId(booking.getId());
                bh.setBusType(booking.getTrip().getBus().getBusType());
                bookingResponses.add(bh);
            }

        }

        return bookingResponses;
    }

}
