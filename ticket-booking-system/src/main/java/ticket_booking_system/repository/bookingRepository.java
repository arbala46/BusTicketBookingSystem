package ticket_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket_booking_system.entity.Booking;

public interface bookingRepository extends JpaRepository<Booking,Long> {
}
