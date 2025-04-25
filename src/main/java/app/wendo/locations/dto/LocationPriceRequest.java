package app.wendo.locations.dto;

import app.wendo.locations.TransportOption;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationPriceRequest {
    private String pickupName;
    private String destinationName;
    private TransportOption transportOption;
    private double price;
}
