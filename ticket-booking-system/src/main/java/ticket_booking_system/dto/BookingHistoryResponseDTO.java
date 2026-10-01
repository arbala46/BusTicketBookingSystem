package ticket_booking_system.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ticket_booking_system.ENUM.BookingStatus;
import ticket_booking_system.ENUM.BusType;
import ticket_booking_system.ENUM.PassengerStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingHistoryResponseDTO {

    private Long userId;

    private String passengerName;

    private Integer age;

    private String busNumber;

    private String busName;

    private String source;

    private String destination;

    private PassengerStatus passengerStatus;

    private BookingStatus bookingStatus;

    private Long bookingId;

    private LocalDate journeyDate;

    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    private BusType busType;

}
