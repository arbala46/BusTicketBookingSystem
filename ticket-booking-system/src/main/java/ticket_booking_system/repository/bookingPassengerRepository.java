package ticket_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket_booking_system.entity.BookingPassenger;

public interface bookingPassengerRepository extends JpaRepository<BookingPassenger,Long> {
}
