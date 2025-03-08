package app.wendo.users.repositories;

import app.wendo.users.models.Driver;
import app.wendo.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    Optional<Driver> findByUser(User user);
}
