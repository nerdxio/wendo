package app.wendo.locations;

import app.wendo.locations.dto.LocationPriceRequest;
import app.wendo.locations.dto.LocationPriceResponse;
import app.wendo.locations.dto.LocationPriceUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/price")
    public ResponseEntity<Map<String, Double>> getPrice(
            @RequestParam Long pickupId,
            @RequestParam Long destinationId,
            @RequestParam TransportOption transportOption) {

        double price = locationService.calculatePrice(pickupId, destinationId, transportOption);
        return ResponseEntity.ok(Map.of("price", price));
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAvailableLocationsByTransportOption(@RequestParam TransportOption transportOption) {
        List<Location> pickups = locationService.getAvailableLocationsByTransportOption(transportOption);
        return ResponseEntity.ok(pickups);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void insertPrice(@RequestBody LocationPriceRequest request) {
        locationService.insertLocationPrice(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePrice(@RequestBody LocationPriceUpdateRequest request) {
        locationService.updateLocationPrice(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePrice(
            @RequestParam Long pickupId,
            @RequestParam Long destinationId,
            @RequestParam TransportOption transportOption) {

        locationService.deleteLocationPrice(pickupId, destinationId, transportOption);
    }

    @GetMapping("/prices")
    public ResponseEntity<List<LocationPriceResponse>> getAllLocationPrices() {
        List<LocationPriceResponse> prices = locationService.getAllLocationPrices();
        return ResponseEntity.ok(prices);
    }
}
