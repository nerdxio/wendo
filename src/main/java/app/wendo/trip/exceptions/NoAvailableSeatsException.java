package app.wendo.trip.exceptions;

import lombok.Getter;

@Getter
public class NoAvailableSeatsException extends RuntimeException {
    
    private final String code = "no-available-seats";
    private final String message;

    public NoAvailableSeatsException(String message) {
        super(message);
        this.message = message;
    }
}
