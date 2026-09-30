package ticket_booking_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket_booking_system.dto.*;
import ticket_booking_system.entity.BookingPassenger;
import java.util.*;
import ticket_booking_system.service.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> booking(@RequestBody BookingRequestDTO bookingRequestDTO)
    {
        return ResponseEntity.ok(bookingService.bookTickets(bookingRequestDTO));
    }


    @PostMapping("fullBookingCancel/{bookingId}")
    public ResponseEntity<BookingCancellationResponseDTO> bookingCancellation(@PathVariable Long bookingId)
    {
        return ResponseEntity.ok(bookingService.fullBookingCancellation(bookingId));
    }

    @GetMapping("bookedPassengers/{bookingId}")
    public ResponseEntity<List<BookedPassengerResponseDTO>> getBookedPassengersDetails(@PathVariable Long bookingId)
    {
        return ResponseEntity.ok(bookingService.getBookedPassengers(bookingId));
    }

    @PostMapping("{bookingId}/cancel-passengers")
    public ResponseEntity<BookingCancellationResponseDTO> partialCancellation(@PathVariable Long bookingId, @RequestBody PartialCancellationRequestDTO partialCancellationRequestDTO)
    {
        return ResponseEntity.ok(bookingService.partialCancellation(bookingId,partialCancellationRequestDTO));
    }

}
