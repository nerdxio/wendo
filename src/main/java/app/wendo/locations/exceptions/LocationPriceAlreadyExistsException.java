package app.wendo.locations.exceptions;

import app.wendo.locations.TransportOption;

public class LocationPriceAlreadyExistsException extends RuntimeException {
    private final String code = "location-price-already-exists";

    public LocationPriceAlreadyExistsException(Long pickupId, Long destinationId, TransportOption transportOption) {
        super("Price already exists for pickup ID: " + pickupId + 
              ", destination ID: " + destinationId + 
              ", transport option: " + transportOption);
    }

    public String getCode() {
        return code;
    }
}