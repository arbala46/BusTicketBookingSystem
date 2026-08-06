package ticket_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket_booking_system.entity.Bus;

public interface busRepository extends JpaRepository<Bus,Long> {

}
