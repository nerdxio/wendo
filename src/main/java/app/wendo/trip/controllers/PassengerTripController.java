package app.wendo.trip.controllers;

import app.wendo.trip.dtos.TripResponse;
import app.wendo.trip.services.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passenger/trips")
@RequiredArgsConstructor
public class PassengerTripController {

    private final TripService tripService;
    
    @GetMapping("/available")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<List<TripResponse>> getAvailableTrips() {
        return ResponseEntity.ok(tripService.getAvailableTrips());
    }
    
    @PostMapping("/{tripId}/join")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<TripResponse> joinTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.joinTrip(tripId));
    }
    
    @PostMapping("/{tripId}/leave")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<TripResponse> leaveTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.leaveTrip(tripId));
    }
}
