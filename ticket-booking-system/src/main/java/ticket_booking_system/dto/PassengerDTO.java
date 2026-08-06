package ticket_booking_system.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ticket_booking_system.ENUM.Gender;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PassengerDTO {

    private Long tripSeatId;

    private String passengerName;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;


}
