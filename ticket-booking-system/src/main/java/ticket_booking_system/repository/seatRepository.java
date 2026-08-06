package ticket_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket_booking_system.entity.Bus;
import ticket_booking_system.entity.Seat;
import java.util.*;

public interface seatRepository extends JpaRepository<Seat,Long> {

    List<Seat> findByBus(Bus bus);

}
