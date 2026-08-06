package ticket_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket_booking_system.entity.Bus;
import ticket_booking_system.entity.Trip;

import java.time.LocalDate;
import java.util.List;

public interface tripRepository extends JpaRepository<Trip,Long> {

    boolean existsByBusAndJourneyDate(Bus bus, LocalDate journeyDate);

    List<Trip> findByBusSourceIgnoreCaseAndBusDestinationIgnoreCaseAndJourneyDate(
            String source,
            String destination,
            LocalDate journeyDate);

}
