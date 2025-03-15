package app.wendo.trip.controllers;

import app.wendo.trip.dtos.CreateTripRequest;
import app.wendo.trip.dtos.TripResponse;
import app.wendo.trip.services.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class DriverTripController {

    private final TripService tripService;
    
    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(request));
    }
    
    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.getTrip(tripId));
    }
    
    @GetMapping
    public ResponseEntity<List<TripResponse>> getMyTrips() {
        return ResponseEntity.ok(tripService.getMyTrips());
    }
    
    @GetMapping("/history")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<TripResponse>> getTripHistory() {
        return ResponseEntity.ok(tripService.getDriverTripHistory());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<TripResponse>> getActiveTrips() {
        return ResponseEntity.ok(tripService.getDriverActiveTrips());
    }

    @PutMapping("/{tripId}/cancel")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TripResponse> cancelTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.cancelTrip(tripId));
    }
    
    @PutMapping("/{tripId}/confirm/{passengerId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TripResponse> confirmTrip(
            @PathVariable Long tripId, 
            @PathVariable Long passengerId) {
        return ResponseEntity.ok(tripService.confirmTrip(tripId, passengerId));
    }
    
    @PutMapping("/{tripId}/start")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TripResponse> startTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.startTrip(tripId));
    }
    
    @PutMapping("/{tripId}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TripResponse> completeTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.completeTrip(tripId));
    }
}
