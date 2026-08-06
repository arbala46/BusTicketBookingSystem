package ticket_booking_system.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ticket_booking_system.ENUM.Gender;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking_passenger")
public class BookingPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @OneToOne
    @JoinColumn(name = "trip_seat_id")
    private TripSeat tripSeat;

    private String passengerName;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

}
