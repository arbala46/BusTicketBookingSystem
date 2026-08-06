package ticket_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket_booking_system.entity.TripSeat;

import java.util.List;

public interface tripSeatRepository extends JpaRepository<TripSeat,Long> {

    List<TripSeat> findByTripId(Long tripId);

    boolean existsById(Long Id);

}
