package app.wendo.users.repositories;

import app.wendo.users.models.Passenger;
import app.wendo.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    
    Optional<Passenger> findByUser(User user);
}
