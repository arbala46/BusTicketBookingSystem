package ticket_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ticket_booking_system.ENUM.BusType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchTripResponseDTO {

    private Long tripId;

    private String busName;

    private String source;

    private String destination;

    private LocalTime departureTime;

    private LocalTime arrivalTime;

    private Long availableSeats;

    private BigDecimal fare;

    private BusType busType;

    private LocalDate journeyDate;

}
