package ticket_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingRequestDTO {

    private Long tripId;

    private List<PassengerDTO> passengers;

}
