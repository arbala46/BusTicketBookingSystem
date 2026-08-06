package ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ticket_booking_system.ENUM.SeatPosition;
import ticket_booking_system.ENUM.SeatStatus;
import ticket_booking_system.ENUM.SeatType;

@Entity
@Table(name="seats")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private Integer seatRow;

    @Enumerated(EnumType.STRING)
    private SeatPosition seatPosition;

    @ManyToOne
    @JoinColumn(name="bus_id")
    private Bus bus;

}
