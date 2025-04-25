package app.wendo.locations.dto;

import app.wendo.locations.TransportOption;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationPriceUpdateRequest {
    private Long pickupId;
    private Long destinationId;
    private TransportOption transportOption;
    private double price;
}