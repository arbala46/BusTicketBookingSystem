package ticket_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket_booking_system.entity.User;

import java.util.List;
import java.util.Optional;

public interface userRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

}
