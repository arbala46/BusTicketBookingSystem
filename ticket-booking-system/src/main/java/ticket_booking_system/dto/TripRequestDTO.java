package ticket_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestDTO {

    private Long busId;
    private LocalDate startDate;
    private LocalDate endDate;

}
