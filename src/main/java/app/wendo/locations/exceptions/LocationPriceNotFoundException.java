package app.wendo.locations.exceptions;

import app.wendo.locations.TransportOption;

public class LocationPriceNotFoundException extends RuntimeException {
    private final String code = "location-price-not-found";

    public LocationPriceNotFoundException(Long pickupId, Long destinationId, TransportOption transportOption) {
        super("Price not found for pickup ID: " + pickupId + 
              ", destination ID: " + destinationId + 
              ", transport option: " + transportOption);
    }

    public LocationPriceNotFoundException(String message) {
        super(message);
    }

    public String getCode() {
        return code;
    }
}