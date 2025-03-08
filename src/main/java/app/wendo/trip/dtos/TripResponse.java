package app.wendo.trip.dtos;

import app.wendo.trip.models.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripResponse {
    private Long id;
    private Long driverId;
    private String driverName;
    private String driverPhoneNumber;
    private String driverProfilePicture;
    private String carType;
    private String licensePlate;
    
    // List of passengers instead of single passenger
    private List<PassengerInfo> passengers;
    private Integer maxPassengers;
    private Integer availableSeats;
    
    private String pickupLocation;
    private Double pickupLatitude;
    private Double pickupLongitude;
    
    private String destination;
    private Double destinationLatitude;
    private Double destinationLongitude;
    
    private BigDecimal estimatedFare;
    private BigDecimal finalFare;
    private Integer estimatedDurationMinutes;
    private Double distanceKilometers;
    
    private TripStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    
    // Inner class to represent passenger information
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PassengerInfo {
        private Long id;
        private String name;
        private String phoneNumber;
        private String profilePicture;
    }
}
