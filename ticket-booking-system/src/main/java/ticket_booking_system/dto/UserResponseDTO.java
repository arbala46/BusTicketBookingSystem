package ticket_booking_system.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ticket_booking_system.ENUM.Gender;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

}
