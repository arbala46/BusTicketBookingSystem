package ticket_booking_system.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ticket_booking_system.ENUM.Gender;
import ticket_booking_system.ENUM.PassengerStatus;
import ticket_booking_system.entity.BookingPassenger;
import ticket_booking_system.entity.Seat;
import ticket_booking_system.entity.TripSeat;

@Data
public class BookedPassengerResponseDTO {

    private Long id;

    private String passengerName;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Long tripSeat_Id;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private PassengerStatus passengerStatus;


}
