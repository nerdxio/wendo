package app.wendo.trip.exceptions;

import lombok.Getter;

@Getter
public class DriverNotAvailableException extends RuntimeException {
    
    private final String code = "driver-not-available";
    private final String message = "Driver is not available";

    public DriverNotAvailableException() {
        super("Driver is not available");
    }
    
    public DriverNotAvailableException(String message) {
        super(message);
    }
}
