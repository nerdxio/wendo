package app.wendo.locations;

import app.wendo.locations.dto.LocationPriceRequest;
import app.wendo.locations.dto.LocationPriceResponse;
import app.wendo.locations.dto.LocationPriceUpdateRequest;
import app.wendo.locations.exceptions.InvalidLocationDataException;
import app.wendo.locations.exceptions.LocationNotFoundException;
import app.wendo.locations.exceptions.LocationPriceAlreadyExistsException;
import app.wendo.locations.exceptions.LocationPriceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationPriceRepository locationPriceRepository;
    private final LocationRepository locationRepository;

    public double calculatePrice(Long pickupId, Long destinationId, TransportOption transportOption) {
        Location pickup = locationRepository.findById(pickupId)
                .orElseThrow(() -> new LocationNotFoundException(pickupId));
        Location destination = locationRepository.findById(destinationId)
                .orElseThrow(() -> new LocationNotFoundException(destinationId));

        return locationPriceRepository
                .findByPickupAndDestinationAndTransportOption(pickup, destination, transportOption)
                .map(LocationPrice::getPrice)
                // If not found, try the reverse direction (destination as pickup and pickup as destination)
                .orElseGet(() -> locationPriceRepository
                        .findByPickupAndDestinationAndTransportOption(destination, pickup, transportOption)
                        .map(LocationPrice::getPrice)
                        .orElseThrow(() -> new LocationPriceNotFoundException(pickupId, destinationId, transportOption)));
    }

    public List<Location> getAvailableLocationsByTransportOption(TransportOption transportOption) {
        List<LocationPrice> prices = locationPriceRepository.findByTransportOption(transportOption);

        return prices.stream()
                .flatMap(price -> Stream.of(price.getPickup(), price.getDestination()))
                .distinct()
                .toList();
    }

    public void insertLocationPrice(LocationPriceRequest request) {
        // Find or create pickup location by name
        Location pickup = findOrCreateByName(request.getPickupName());

        // Find or create destination location by name
        Location destination = findOrCreateByName(request.getDestinationName());

        // Check if price already exists for this combination
        if (locationPriceRepository.existsByPickupIdAndDestinationIdAndTransportOption(
                pickup.getId(), destination.getId(), request.getTransportOption())) {
            throw new LocationPriceAlreadyExistsException(
                pickup.getId(), destination.getId(), request.getTransportOption());
        }

        // Check if reverse direction price already exists
        boolean reverseExists = locationPriceRepository.existsByPickupIdAndDestinationIdAndTransportOption(
                destination.getId(), pickup.getId(), request.getTransportOption());

        // Create and save the location price
        LocationPrice price = new LocationPrice();
        price.setPickup(pickup);
        price.setDestination(destination);
        price.setTransportOption(request.getTransportOption());
        price.setPrice(request.getPrice());

        locationPriceRepository.save(price);

        // Create and save the reverse direction price if it doesn't exist
        if (!reverseExists) {
            LocationPrice reversePrice = new LocationPrice();
            reversePrice.setPickup(destination);
            reversePrice.setDestination(pickup);
            reversePrice.setTransportOption(request.getTransportOption());
            reversePrice.setPrice(request.getPrice());

            locationPriceRepository.save(reversePrice);
        }
    }

    private Location findOrCreateLocation(Long id, String name) {
        if (id != null) {
            return locationRepository.findById(id)
                    .orElseGet(() -> findOrCreateByName(name));
        }

        return findOrCreateByName(name);
    }

    private Location findOrCreateByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidLocationDataException("Location name must be provided if ID is not provided");
        }

        return locationRepository.findByName(name)
                .orElseGet(() -> {
                    Location newLocation = new Location();
                    newLocation.setName(name);
                    return locationRepository.save(newLocation);
                });
    }

    public void updateLocationPrice(LocationPriceUpdateRequest request) {
        Location pickup = locationRepository.findById(request.getPickupId())
                .orElseThrow(() -> new LocationNotFoundException(request.getPickupId()));
        Location destination = locationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new LocationNotFoundException(request.getDestinationId()));

        LocationPrice price = locationPriceRepository
                .findByPickupAndDestinationAndTransportOption(pickup, destination, request.getTransportOption())
                .orElseThrow(() -> new LocationPriceNotFoundException(
                    request.getPickupId(), request.getDestinationId(), request.getTransportOption()));

        price.setPrice(request.getPrice());
        locationPriceRepository.save(price);
    }

    public void deleteLocationPrice(Long pickupId, Long destinationId, TransportOption transportOption) {
        Location pickup = locationRepository.findById(pickupId)
                .orElseThrow(() -> new LocationNotFoundException(pickupId));
        Location destination = locationRepository.findById(destinationId)
                .orElseThrow(() -> new LocationNotFoundException(destinationId));

        LocationPrice price = locationPriceRepository
                .findByPickupAndDestinationAndTransportOption(pickup, destination, transportOption)
                .orElseThrow(() -> new LocationPriceNotFoundException(pickupId, destinationId, transportOption));

        locationPriceRepository.delete(price);
    }

    public List<LocationPriceResponse> getAllLocationPrices() {
        List<LocationPrice> prices = locationPriceRepository.findAll();

        return prices.stream()
                .map(price -> LocationPriceResponse.builder()
                        .id(price.getId())
                        .pickupId(price.getPickup().getId())
                        .pickupName(price.getPickup().getName())
                        .destinationId(price.getDestination().getId())
                        .destinationName(price.getDestination().getName())
                        .transportOption(price.getTransportOption())
                        .price(price.getPrice())
                        .build())
                .collect(Collectors.toList());
    }
}
