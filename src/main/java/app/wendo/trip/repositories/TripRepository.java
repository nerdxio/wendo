package app.wendo.trip.repositories;

import app.wendo.trip.models.Trip;
import app.wendo.trip.models.TripStatus;
import app.wendo.users.models.Driver;
import app.wendo.users.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByDriverOrderByCreatedAtDesc(Driver driver);

    List<Trip> findByPassengersContainingOrderByCreatedAtDesc(Passenger passenger);

    List<Trip> findByStatusOrderByCreatedAtDesc(TripStatus status);

    List<Trip> findByStatusIn(List<TripStatus> statuses);
}
