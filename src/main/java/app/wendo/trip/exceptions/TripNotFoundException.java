package app.wendo.trip.exceptions;

import lombok.Getter;

@Getter
public class TripNotFoundException extends RuntimeException {
    
    private final String code = "trip-not-found";
    private final String message = "Trip not found";

    public TripNotFoundException() {
        super("Trip not found");
    }
    
    public TripNotFoundException(String message) {
        super(message);
    }
}
