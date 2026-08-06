package ticket_booking_system.dto;

import lombok.Data;
import ticket_booking_system.ENUM.BookingStatus;

import java.math.BigDecimal;

@Data
public class BookingResponseDTO {

    private Long bookingId;

    private Long tripId;

    private Integer passengersCount;

    private BookingStatus bookingStatus;

    private String message;

    private BigDecimal totalAmount;

}
