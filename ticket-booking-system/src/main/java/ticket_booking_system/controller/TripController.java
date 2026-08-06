package ticket_booking_system.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket_booking_system.dto.SearchTripResponseDTO;
import ticket_booking_system.dto.SeatLayoutResponseDTO;
import ticket_booking_system.dto.TripRequestDTO;
import ticket_booking_system.exception.BusNotFoundException;
import ticket_booking_system.service.TripService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    TripService tripService;

    @PostMapping("/schedule")
    public String tripSchedule(@RequestBody TripRequestDTO tripRequestDTO)
    {
        try{
            String response = tripService.createTrip(tripRequestDTO);
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusNotFoundException(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchTripResponseDTO>> searchTrips(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam LocalDate journeyDate
            )
    {
        return ResponseEntity.ok(tripService.searchTrips(source,destination,journeyDate));
    }

    @GetMapping("/{tripId}/seats")
    public ResponseEntity<List<SeatLayoutResponseDTO>> getSeats(@PathVariable Long tripId)
    {
        return ResponseEntity.ok(tripService.searchSeats(tripId));
    }

}
