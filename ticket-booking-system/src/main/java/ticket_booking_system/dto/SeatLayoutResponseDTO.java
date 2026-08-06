package ticket_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ticket_booking_system.ENUM.SeatPosition;
import ticket_booking_system.ENUM.SeatStatus;
import ticket_booking_system.ENUM.SeatType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatLayoutResponseDTO {

    private Long seatId;
    private String seatNumber;
    private SeatType seatType;
    private SeatPosition seatPosition;
    private Integer seatRow;
    private SeatStatus seatStatus;

}
