package app.wendo.locations.exceptions;

public class LocationNotFoundException extends RuntimeException {
    private final String code = "location-not-found";

    public LocationNotFoundException(Long locationId) {
        super("Location not found with ID: " + locationId);
    }

    public String getCode() {
        return code;
    }
}