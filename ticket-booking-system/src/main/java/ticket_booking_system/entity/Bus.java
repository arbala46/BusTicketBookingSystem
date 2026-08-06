package ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.*;
import ticket_booking_system.ENUM.BusType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Entity
@Table(name="buses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String busNumber;

    private String busName;

    private String source;

    private String destination;

    private LocalTime  departureTime;

    private LocalTime arrivalTime;

    private BigDecimal fare;

    @Enumerated(EnumType.STRING)
    private BusType busType;

    private Integer totalSeats;

    @OneToMany(mappedBy = "bus")
    private List<Seat> seats;

}
