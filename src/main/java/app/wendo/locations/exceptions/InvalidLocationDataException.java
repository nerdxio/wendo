package app.wendo.locations.exceptions;

public class InvalidLocationDataException extends RuntimeException {
    private final String code = "invalid-location-data";

    public InvalidLocationDataException(String message) {
        super(message);
    }

    public String getCode() {
        return code;
    }
}