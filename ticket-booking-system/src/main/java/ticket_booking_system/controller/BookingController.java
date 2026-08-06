package ticket_booking_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ticket_booking_system.dto.BookingRequestDTO;
import ticket_booking_system.dto.BookingResponseDTO;
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

}
