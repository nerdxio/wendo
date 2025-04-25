package app.wendo.locations.dto;

import app.wendo.locations.TransportOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationPriceResponse {
    private Long id;
    private Long pickupId;
    private String pickupName;
    private Long destinationId;
    private String destinationName;
    private TransportOption transportOption;
    private double price;
}