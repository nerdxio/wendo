package app.wendo.trip.services;

import app.wendo.security.SecurityUtils;
import app.wendo.trip.dtos.CreateTripRequest;
import app.wendo.trip.dtos.TripResponse;
import app.wendo.trip.exceptions.DriverNotAvailableException;
import app.wendo.trip.exceptions.InvalidTripStatusTransitionException;
import app.wendo.trip.exceptions.NoAvailableSeatsException;
import app.wendo.trip.exceptions.TripNotFoundException;
import app.wendo.trip.models.Trip;
import app.wendo.trip.models.TripStatus;
import app.wendo.trip.repositories.TripRepository;
import app.wendo.users.models.Driver;
import app.wendo.users.models.Passenger;
import app.wendo.users.models.User;
import app.wendo.users.repositories.DriverRepository;
import app.wendo.users.repositories.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final SecurityUtils securityUtils;

    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        // Get the driver entity associated with the current user
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Driver record not found"));

        // Check if driver is available
        if (!driver.getIsAvailable()) {
            throw new DriverNotAvailableException("Driver is not active or available");
        }

        // Check if driver already has an active trip
        if (driver.hasActiveTrip()) {
            throw new DriverNotAvailableException(
                    "Driver already has an active trip with status: " + driver.getActiveTrip().getStatus());
        }

        // Create new trip with default values for passenger capacity
        Trip trip = Trip.builder()
                .driver(driver)
                .pickupLocation(request.getPickupLocation())
                .destination(request.getDestination())
                .maxPassengers(10)
                .availableSeats(10)
                .status(TripStatus.CREATED)
                .build();

        // Set start date if provided
        if (request.getStartDate() != null) {
            trip.setStartedAt(request.getStartDate());
        }

        // Save the trip
        Trip savedTrip = tripRepository.save(trip);

        // Update driver availability status
        driver.setIsAvailable(false);
        driverRepository.save(driver);

        return convertToTripResponse(savedTrip);
    }

    @Transactional
    public TripResponse joinTrip(Long tripId) {
        User currentUser = securityUtils.getCurrentUser();
        Passenger passenger = passengerRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Passenger record not found"));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        // Validate trip is in correct status
        if (trip.getStatus() != TripStatus.CREATED && trip.getStatus() != TripStatus.SEARCHING) {
            throw new InvalidTripStatusTransitionException("Cannot join trip with status: " + trip.getStatus(),
                    trip.getStatus(), trip.getStatus());
        }

        // Check if there are available seats
        if (trip.getAvailableSeats() <= 0) {
            throw new NoAvailableSeatsException("No available seats on this trip");
        }

        // Add passenger to trip
        boolean added = trip.addPassenger(passenger);
        if (!added) {
            throw new RuntimeException("Failed to add passenger to trip");
        }

        // If this is the first passenger, change status to SEARCHING
        if (trip.getStatus() == TripStatus.CREATED) {
            trip.setStatus(TripStatus.SEARCHING);
        }

        Trip savedTrip = tripRepository.save(trip);
        return convertToTripResponse(savedTrip);
    }

    @Transactional
    public TripResponse leaveTrip(Long tripId) {
        User currentUser = securityUtils.getCurrentUser();
        Passenger passenger = passengerRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Passenger record not found"));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        // Validate trip is in correct status
        if (trip.getStatus() != TripStatus.CREATED && trip.getStatus() != TripStatus.SEARCHING) {
            throw new InvalidTripStatusTransitionException("Cannot leave trip with status: " + trip.getStatus(),
                    trip.getStatus(), trip.getStatus());
        }

        // Remove passenger from trip
        boolean removed = trip.removePassenger(passenger);
        if (!removed) {
            throw new RuntimeException("Passenger is not in this trip");
        }

        // If no more passengers, change status back to CREATED
        if (trip.getPassengers().isEmpty() && trip.getStatus() == TripStatus.SEARCHING) {
            trip.setStatus(TripStatus.CREATED);
        }

        Trip savedTrip = tripRepository.save(trip);
        return convertToTripResponse(savedTrip);
    }

    @Transactional
    public TripResponse cancelTrip(Long tripId) {
        User currentUser = securityUtils.getCurrentUser();
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Driver record not found"));

        Trip trip = findTripAndValidateOwnership(tripId, driver);

        // Can only cancel if not already completed or cancelled
        if (trip.getStatus() == TripStatus.COMPLETED || trip.getStatus() == TripStatus.CANCELLED) {
            throw new InvalidTripStatusTransitionException(
                    trip.getStatus(), TripStatus.CANCELLED);
        }

        trip.setStatus(TripStatus.CANCELLED);

        // Make the driver available again
        driver.setIsAvailable(true);
        driverRepository.save(driver);

        return convertToTripResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripResponse confirmTrip(Long tripId, Long passengerId) {
        User currentUser = securityUtils.getCurrentUser();
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Driver record not found"));

        Trip trip = findTripAndValidateOwnership(tripId, driver);

        // Validate current status
        if (trip.getStatus() != TripStatus.SEARCHING) {
            throw new InvalidTripStatusTransitionException(
                    trip.getStatus(), TripStatus.CONFIRMED);
        }

        trip.setStatus(TripStatus.CONFIRMED);

        return convertToTripResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripResponse startTrip(Long tripId) {
        User currentUser = securityUtils.getCurrentUser();
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Driver record not found"));

        Trip trip = findTripAndValidateOwnership(tripId, driver);

        // Validate current status
        if (trip.getStatus() != TripStatus.CONFIRMED) {
            throw new InvalidTripStatusTransitionException(
                    trip.getStatus(), TripStatus.IN_PROGRESS);
        }

        trip.setStatus(TripStatus.IN_PROGRESS);
        trip.setStartedAt(LocalDateTime.now());

        return convertToTripResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripResponse completeTrip(Long tripId) {
        User currentUser = securityUtils.getCurrentUser();
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Driver record not found"));

        Trip trip = findTripAndValidateOwnership(tripId, driver);

        // Validate current status
        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new InvalidTripStatusTransitionException(
                    trip.getStatus(), TripStatus.COMPLETED);
        }

        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletedAt(LocalDateTime.now());

        // Make driver available again
        driver.setIsAvailable(true);
        driverRepository.save(driver);

        return convertToTripResponse(tripRepository.save(trip));
    }

    public TripResponse getTrip(Long tripId) {
        User currentUser = securityUtils.getCurrentUser();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        // Check if user is authorized to view this trip
        Driver driver = driverRepository.findByUser(currentUser).orElse(null);
        Passenger passenger = passengerRepository.findByUser(currentUser).orElse(null);

        if ((driver != null && trip.getDriver().equals(driver)) ||
                (passenger != null && trip.getPassengers().contains(passenger))) {
            // User is either the driver or a passenger of this trip
            return convertToTripResponse(trip);
        }

        throw new RuntimeException("You are not authorized to view this trip");
    }

    public List<TripResponse> getMyTrips() {
        User currentUser = securityUtils.getCurrentUser();

        List<Trip> trips;

        // Check if the user is a driver
        Driver driver = driverRepository.findByUser(currentUser).orElse(null);
        if (driver != null) {
            trips = tripRepository.findByDriverOrderByCreatedAtDesc(driver);
        } else {
            // Check if the user is a passenger
            Passenger passenger = passengerRepository.findByUser(currentUser).orElse(null);
            if (passenger != null) {
                trips = new ArrayList<>(passenger.getTrips());
            } else {
                throw new RuntimeException("User must be either a driver or passenger");
            }
        }

        return trips.stream()
                .map(this::convertToTripResponse)
                .collect(Collectors.toList());
    }

    public List<TripResponse> getAvailableTrips() {
        // Find trips that are in CREATED or SEARCHING status and have available seats
        List<Trip> availableTrips = tripRepository.findByStatusIn(List.of(TripStatus.CREATED, TripStatus.SEARCHING))
                .stream()
                .filter(trip -> trip.getAvailableSeats() > 0)
                .collect(Collectors.toList());

        return availableTrips.stream()
                .map(this::convertToTripResponse)
                .collect(Collectors.toList());
    }

    private Trip findTripAndValidateOwnership(Long tripId, Driver driver) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        // Only the driver who created the trip can modify it
        if (!trip.getDriver().equals(driver)) {
            throw new RuntimeException("You are not authorized to modify this trip");
        }

        return trip;
    }

    private TripResponse convertToTripResponse(Trip trip) {
        TripResponse response = new TripResponse();
        response.setId(trip.getId());
        response.setStatus(trip.getStatus());

        // Driver info
        Driver driver = trip.getDriver();
        User driverUser = driver.getUser();
        response.setDriverId(driver.getId());
        response.setDriverName(driverUser.getFullName());
        response.setDriverPhoneNumber(driverUser.getPhoneNumber());
        response.setDriverProfilePicture(driverUser.getProfileImageUrl());

        // Passenger info
        List<TripResponse.PassengerInfo> passengerInfoList = new ArrayList<>();
        if (trip.getPassengers() != null) {
            for (Passenger passenger : trip.getPassengers()) {
                User passengerUser = passenger.getUser();
                TripResponse.PassengerInfo passengerInfo = TripResponse.PassengerInfo.builder()
                        .id(passenger.getId())
                        .name(passengerUser.getFullName())
                        .phoneNumber(passengerUser.getPhoneNumber())
                        .profilePicture(passengerUser.getProfileImageUrl())
                        .build();
                passengerInfoList.add(passengerInfo);
            }
        }
        response.setPassengers(passengerInfoList);
        response.setMaxPassengers(trip.getMaxPassengers());
        response.setAvailableSeats(trip.getAvailableSeats());

        // Trip details
        response.setPickupLocation(trip.getPickupLocation());
        response.setDestination(trip.getDestination());
        response.setEstimatedDurationMinutes(trip.getEstimatedDurationMinutes());
        response.setDistanceKilometers(trip.getDistanceKilometers());
        response.setCreatedAt(trip.getCreatedAt());
        response.setStartedAt(trip.getStartedAt());
        response.setCompletedAt(trip.getCompletedAt());

        return response;
    }
}
