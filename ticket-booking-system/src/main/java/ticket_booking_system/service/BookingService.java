package ticket_booking_system.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ticket_booking_system.ENUM.BookingStatus;
import ticket_booking_system.ENUM.SeatPosition;
import ticket_booking_system.ENUM.SeatStatus;
import ticket_booking_system.dto.BookingRequestDTO;
import ticket_booking_system.dto.BookingResponseDTO;
import ticket_booking_system.dto.PassengerDTO;
import ticket_booking_system.entity.*;
import ticket_booking_system.exception.AdjacentSeatException;
import ticket_booking_system.exception.BookingException;
import ticket_booking_system.exception.TripNotFoundException;
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
    public BookingResponseDTO bookTickets(BookingRequestDTO bookingRequestDTO)
    {

       Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();

        User user;
       if(authentication!=null && authentication.isAuthenticated())
       {
           Object principal = authentication.getPrincipal();
           if(principal instanceof User) {
               user = (User) principal;
           } else throw new BookingException("Authenticated principal is not a valid User.");
       }else throw new BookingException("User is not authenticated.");

        Trip trip = tripRepository.findById(bookingRequestDTO.getTripId())
                .orElseThrow(()->{
                    log.error("Trip Id {} not found",bookingRequestDTO.getTripId());
                     return new TripNotFoundException("TripId " + bookingRequestDTO.getTripId()+" not found");
                });

        if(bookingRequestDTO.getPassengers()==null || bookingRequestDTO.getPassengers().isEmpty())
        {
            log.error("Passenger list is empty");
            throw new BookingException("Passenger list is empty");
        }

        if(trip.getAvailableSeats()<bookingRequestDTO.getPassengers().size())
        {
            log.error("No Available Seats");
            throw new BookingException("No Available Seats");
        }

        Set<Long> SeatIds = new HashSet<>();
        for(PassengerDTO passenger : bookingRequestDTO.getPassengers())
        {
            if(!SeatIds.add(passenger.getTripSeatId()))
            {
                log.error("Seat ID "+passenger.getTripSeatId()+" is duplicate");
                throw new BookingException("Seat ID "+passenger.getTripSeatId()+" is duplicate");
            }

        }

        Map<Long,TripSeat> tripSeatMap = new HashMap<>();
        for(PassengerDTO passenger : bookingRequestDTO.getPassengers())
        {
            TripSeat tripSeat = tripSeatRepository.findById(passenger.getTripSeatId()).
                    orElseThrow(()->{
                        log.error("Trip SeatId {} not found",passenger.getTripSeatId());
                        return new BookingException("TripSeatId     " + passenger.getTripSeatId()+" not found");
                    });
            tripSeatMap.put(tripSeat.getId(),tripSeat);
        }

        for(PassengerDTO passenger : bookingRequestDTO.getPassengers())
        {
                Long SeatId = passenger.getTripSeatId();
                if(!tripSeatMap.get(SeatId).getTrip().getId().equals(trip.getId()))
                {
                    log.error(tripSeatMap.get(SeatId).getId()+" this TripSeat is not belongs to "+trip.getId()+" this trip");
                    throw new BookingException(tripSeatMap.get(SeatId).getId()+" this TripSeat is not belongs to "+trip.getId()+" this trip");
                }

                if(tripSeatMap.get(SeatId).getSeatStatus()!= SeatStatus.AVAILABLE)
                {
                    log.error("Selected seat is already booked. Please choose another seat.");
                    throw new BookingException("Selected seat is already booked. Please choose another seat.");
                }
        }

        validateGenderBasedSeatAllocation(bookingRequestDTO ,tripSeatMap);

        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setTrip(trip);
        booking.setUser(user);

        List<BookingPassenger> passengerList = new ArrayList<>();
        List<TripSeat> updatedSeats = new ArrayList<>();
        for(PassengerDTO passenger : bookingRequestDTO.getPassengers())
        {
            Long seatId = passenger.getTripSeatId();
            TripSeat tripseat = tripSeatMap.get(seatId);
            BookingPassenger bookingPassenger = new BookingPassenger();
            bookingPassenger.setBooking(booking);
            bookingPassenger.setPassengerName(passenger.getPassengerName());
            bookingPassenger.setAge(passenger.getAge());
            bookingPassenger.setGender(passenger.getGender());
            bookingPassenger.setTripSeat(tripseat);
            passengerList.add(bookingPassenger);
        }

        BigDecimal totalAmt = new BigDecimal(0);
        try{
            totalAmt = trip.getBus().getFare().multiply(new BigDecimal(passengerList.size()));
            log.info("Total payable amount: "+totalAmt);
            log.info("Payment is inprogess..... ");
            Thread.sleep(3000);
            log.info("Payment Completed Successfully, Have a safe journey!");
        }catch (InterruptedException ex)
        {
            throw new RuntimeException("Payment Failed");
        }

        for(PassengerDTO passenger : bookingRequestDTO.getPassengers())
        {
            Long seatId = passenger.getTripSeatId();
            TripSeat tripseat = tripSeatMap.get(seatId);
            tripseat.setSeatStatus(SeatStatus.BOOKED);
            updatedSeats.add(tripseat);
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        bookingPassengerRepository.saveAll(passengerList);
        tripSeatRepository.saveAll(updatedSeats);
        trip.setAvailableSeats(trip.getAvailableSeats()-bookingRequestDTO.getPassengers().size());
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

    public void validateGenderBasedSeatAllocation(BookingRequestDTO request , Map<Long,TripSeat> tripSeatMap)
    {

        List<TripSeat> tripSeats = tripSeatRepository.findByTripId(request.getTripId());

        for(PassengerDTO passenger : request.getPassengers())
        {
            TripSeat CurrentSeat = tripSeatMap.get(passenger.getTripSeatId());
            SeatPosition seatPosition = CurrentSeat.getSeat().getSeatPosition();
            List<SeatPosition> adjacentPositions = new ArrayList<>();

            switch (seatPosition)
            {
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

            for(SeatPosition adjacentPosition : adjacentPositions)
            {
                for(TripSeat ts : tripSeats)
                {
                    if(ts.getSeat().getSeatRow() == CurrentSeat.getSeat().getSeatRow() &&
                       ts.getSeat().getSeatPosition() == adjacentPosition)
                    {
                        if(ts.getSeatStatus()!=SeatStatus.AVAILABLE)
                        {
                            BookingPassenger bookingPassenger = bookingPassengerRepository.findByTripSeatId(ts.getId());
                            if(bookingPassenger != null && bookingPassenger.getGender() != passenger.getGender())
                            {
                                log.error("Adjacent Seat is booked by "
                                        + bookingPassenger.getGender()+" Kindly book another seat!");
                                throw new AdjacentSeatException("Adjacent Seat is booked by "
                                        + bookingPassenger.getGender()+" Kindly book another seat!");
                            }
                        }
                    }
                }

            }


        }



    }

}
