package ticket_booking_system.dto;

import lombok.Data;
import java.util.Set;

@Data
public class PartialCancellationRequestDTO {

    private Set<Long> passengersIds;

}
