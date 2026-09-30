package ticket_booking_system.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ticket_booking_system.ENUM.BookingStatus;
import ticket_booking_system.entity.BookingPassenger;
import ticket_booking_system.entity.Trip;
import ticket_booking_system.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCancellationResponseDTO {

    private LocalDateTime cancelledAt;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    private Long passengersCount;

    private String message;

}
