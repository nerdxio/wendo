package app.wendo.trip.exceptions;

import app.wendo.trip.models.TripStatus;
import lombok.Getter;

@Getter
public class InvalidTripStatusTransitionException extends RuntimeException {
    
    private final String code = "invalid-trip-status-transition";
    private final TripStatus currentStatus;
    private final TripStatus targetStatus;

    public InvalidTripStatusTransitionException(TripStatus currentStatus, TripStatus targetStatus) {
        super("Cannot transition from " + currentStatus + " to " + targetStatus);
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }
    
    public InvalidTripStatusTransitionException(String message, TripStatus currentStatus, TripStatus targetStatus) {
        super(message);
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }
}
