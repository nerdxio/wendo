package app.wendo.locations;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationPriceRepository extends JpaRepository<LocationPrice, Long> {
    Optional<LocationPrice> findByPickupAndDestinationAndTransportOption(
            Location pickup,
            Location destination,
            TransportOption transportOption
    );

    List<LocationPrice> findByTransportOption(TransportOption transportOption);

    boolean existsByPickupIdAndDestinationIdAndTransportOption(Long pickupId, Long destinationId, TransportOption transportOption);
}