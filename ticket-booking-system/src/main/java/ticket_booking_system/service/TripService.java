package ticket_booking_system.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ticket_booking_system.ENUM.SeatStatus;
import ticket_booking_system.dto.SearchTripResponseDTO;
import ticket_booking_system.dto.SeatLayoutResponseDTO;
import ticket_booking_system.dto.TripRequestDTO;
import ticket_booking_system.entity.Bus;
import ticket_booking_system.entity.Seat;
import ticket_booking_system.entity.Trip;
import ticket_booking_system.entity.TripSeat;
import ticket_booking_system.exception.BusNotFoundException;
import ticket_booking_system.exception.TripNotFoundException;
import ticket_booking_system.repository.busRepository;
import ticket_booking_system.repository.seatRepository;
import ticket_booking_system.repository.tripRepository;
import ticket_booking_system.repository.tripSeatRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class TripService {

    @Autowired
    busRepository busRepository;

    @Autowired
    tripRepository tripRepository;

    @Autowired
    seatRepository seatRepository;

    @Autowired
    tripSeatRepository tripSeatRepository;

    @Transactional
    public String createTrip(TripRequestDTO tripRequest)
    {

        Bus bus = busRepository.findById(tripRequest.getBusId()).
                orElseThrow(()-> {log.error("{} BusID not found",tripRequest.getBusId());
                    return new RuntimeException(tripRequest.getBusId()+" BusID not found: ");});

        LocalDate startDate = tripRequest.getStartDate();
        LocalDate endDate = tripRequest.getEndDate();

        if(endDate.isBefore(startDate))
        {
            log.error("StartDate is grater then EndDate");
            throw new RuntimeException("StartDate is grater then EndDate");
        }
        List<Trip> trips = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate))
        {
            Trip trip = new Trip();
            trip.setBus(bus);
            trip.setJourneyDate(currentDate);
            trip.setAvailableSeats((long) bus.getTotalSeats());
            if(tripRepository.existsByBusAndJourneyDate(bus,currentDate))
            {
                currentDate = currentDate.plusDays(1);
                continue;
            }
            trips.add(trip);
            currentDate = currentDate.plusDays(1);
        }
        if(trips.isEmpty())
        {
            log.warn("All requested trips already exist for Bus ID {}", bus.getId());
            return "All requested trips already exist.";
        }
        tripRepository.saveAll(trips);

        List<TripSeat> tripSeats = new ArrayList<>();

        List<Seat> seats = seatRepository.findByBus(bus);
        for(Trip trip: trips)
        {
            for(Seat seat : seats)
            {
                TripSeat tripSeat = new TripSeat();
                tripSeat.setTrip(trip);
                tripSeat.setSeat(seat);
                tripSeat.setSeatStatus(SeatStatus.AVAILABLE);

                tripSeats.add(tripSeat);
            }

        }
        tripSeatRepository.saveAll(tripSeats);


        log.info("{} Trips and {} TripSeats created Successfully! ",
                trips.size(),
                tripSeats.size());

        return trips.size()+" Trips "+ "and "+tripSeats.size()+" TripSeats has been created Successfully!";
    }

    public List<SearchTripResponseDTO> searchTrips(String source,String destination, LocalDate journeyDate)
    {

        List<Trip> trips = tripRepository.findByBusSourceIgnoreCaseAndBusDestinationIgnoreCaseAndJourneyDate(
                source,
                destination,
                journeyDate
        );

        if(trips.isEmpty())
        {
            throw new BusNotFoundException("No buses available for this route "
                    +source+" to "+destination+" on "+ journeyDate);
        }

        List<SearchTripResponseDTO> searchResults  = new ArrayList<>();

        for(Trip trip:trips)
        {
            SearchTripResponseDTO response = new SearchTripResponseDTO();
            response.setTripId(trip.getId());
            response.setBusName(trip.getBus().getBusName());
            response.setAvailableSeats(trip.getAvailableSeats());
            response.setFare(trip.getBus().getFare());
            response.setBusType(trip.getBus().getBusType());
            response.setArrivalTime(trip.getBus().getArrivalTime());
            response.setDepartureTime(trip.getBus().getDepartureTime());
            response.setSource(trip.getBus().getSource());
            response.setDestination(trip.getBus().getDestination());
            response.setJourneyDate(trip.getJourneyDate());
            searchResults .add(response);
        }

        return searchResults;

    }

    public List<SeatLayoutResponseDTO> searchSeats(Long tripId)
    {

        List<TripSeat> seats = tripSeatRepository.findByTripId(tripId);

        if(seats.isEmpty())
        {
            log.error("Trip Id {} Not Found",tripId);
            throw new TripNotFoundException("Trip Id "+tripId+" Not Found");
        }

        List<SeatLayoutResponseDTO> results = new ArrayList<>();

        for(TripSeat ts: seats)
        {
            SeatLayoutResponseDTO response = new SeatLayoutResponseDTO();
            Seat seat = ts.getSeat();

            response.setSeatId(ts.getId());
            response.setSeatPosition(seat.getSeatPosition());
            response.setSeatNumber(seat.getSeatNumber());
            response.setSeatRow(seat.getSeatRow());
            response.setSeatType(seat.getSeatType());
            response.setSeatStatus(ts.getSeatStatus());

            results.add(response);
        }

        return results;

    }

}
