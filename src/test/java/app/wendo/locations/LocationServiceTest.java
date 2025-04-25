package app.wendo.locations;

import app.wendo.locations.dto.LocationPriceRequest;
import app.wendo.locations.exceptions.LocationNotFoundException;
import app.wendo.locations.exceptions.LocationPriceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationPriceRepository locationPriceRepository;

    @InjectMocks
    private LocationService locationService;

    private Location location1;
    private Location location2;
    private LocationPrice locationPrice;

    @BeforeEach
    void setUp() {
        // Setup test data
        location1 = new Location();
        location1.setId(1L);
        location1.setName("Location 1");

        location2 = new Location();
        location2.setId(2L);
        location2.setName("Location 2");

        locationPrice = new LocationPrice();
        locationPrice.setId(1L);
        locationPrice.setPickup(location1);
        locationPrice.setDestination(location2);
        locationPrice.setPrice(100.0);
        locationPrice.setTransportOption(TransportOption.INTERNAL_VAN);
    }

    @Test
    void calculatePrice_ForwardDirection_ReturnsPrice() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location1));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(location2));
        when(locationPriceRepository.findByPickupAndDestinationAndTransportOption(
                location1, location2, TransportOption.INTERNAL_VAN))
                .thenReturn(Optional.of(locationPrice));

        // Act
        double price = locationService.calculatePrice(1L, 2L, TransportOption.INTERNAL_VAN);

        // Assert
        assertEquals(100.0, price);
        verify(locationPriceRepository).findByPickupAndDestinationAndTransportOption(
                location1, location2, TransportOption.INTERNAL_VAN);
        verify(locationPriceRepository, never()).findByPickupAndDestinationAndTransportOption(
                location2, location1, TransportOption.INTERNAL_VAN);
    }

    @Test
    void calculatePrice_ReverseDirection_ReturnsPrice() {
        // Arrange
        when(locationRepository.findById(2L)).thenReturn(Optional.of(location2));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location1));
        when(locationPriceRepository.findByPickupAndDestinationAndTransportOption(
                location2, location1, TransportOption.INTERNAL_VAN))
                .thenReturn(Optional.empty());
        when(locationPriceRepository.findByPickupAndDestinationAndTransportOption(
                location1, location2, TransportOption.INTERNAL_VAN))
                .thenReturn(Optional.of(locationPrice));

        // Act
        double price = locationService.calculatePrice(2L, 1L, TransportOption.INTERNAL_VAN);

        // Assert
        assertEquals(100.0, price);
        verify(locationPriceRepository).findByPickupAndDestinationAndTransportOption(
                location2, location1, TransportOption.INTERNAL_VAN);
        verify(locationPriceRepository).findByPickupAndDestinationAndTransportOption(
                location1, location2, TransportOption.INTERNAL_VAN);
    }

    @Test
    void calculatePrice_BothDirectionsNotFound_ThrowsException() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location1));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(location2));
        when(locationPriceRepository.findByPickupAndDestinationAndTransportOption(
                location1, location2, TransportOption.INTERNAL_VAN))
                .thenReturn(Optional.empty());
        when(locationPriceRepository.findByPickupAndDestinationAndTransportOption(
                location2, location1, TransportOption.INTERNAL_VAN))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LocationPriceNotFoundException.class, () -> 
            locationService.calculatePrice(1L, 2L, TransportOption.INTERNAL_VAN)
        );
        verify(locationPriceRepository).findByPickupAndDestinationAndTransportOption(
                location1, location2, TransportOption.INTERNAL_VAN);
        verify(locationPriceRepository).findByPickupAndDestinationAndTransportOption(
                location2, location1, TransportOption.INTERNAL_VAN);
    }

    @Test
    void calculatePrice_LocationNotFound_ThrowsException() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location1));
        when(locationRepository.findById(3L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LocationNotFoundException.class, () -> 
            locationService.calculatePrice(1L, 3L, TransportOption.INTERNAL_VAN)
        );
        verify(locationRepository).findById(1L);
        verify(locationRepository).findById(3L);
        verifyNoInteractions(locationPriceRepository);
    }

    @Test
    void insertLocationPrice_CreatesBidirectionalRoutes() {
        // Arrange
        LocationPriceRequest request = new LocationPriceRequest();
        request.setPickupName("Location 1");
        request.setDestinationName("Location 2");
        request.setTransportOption(TransportOption.INTERNAL_VAN);
        request.setPrice(100.0);

        when(locationRepository.findByName("Location 1")).thenReturn(Optional.of(location1));
        when(locationRepository.findByName("Location 2")).thenReturn(Optional.of(location2));
        when(locationPriceRepository.existsByPickupIdAndDestinationIdAndTransportOption(1L, 2L, TransportOption.INTERNAL_VAN))
            .thenReturn(false);
        when(locationPriceRepository.existsByPickupIdAndDestinationIdAndTransportOption(2L, 1L, TransportOption.INTERNAL_VAN))
            .thenReturn(false);

        ArgumentCaptor<LocationPrice> priceCaptor = ArgumentCaptor.forClass(LocationPrice.class);

        // Act
        locationService.insertLocationPrice(request);

        // Assert
        verify(locationPriceRepository, times(2)).save(priceCaptor.capture());

        // Get the two captured LocationPrice objects
        List<LocationPrice> savedPrices = priceCaptor.getAllValues();
        assertEquals(2, savedPrices.size());

        // First saved price (original direction)
        LocationPrice originalPrice = savedPrices.get(0);
        assertEquals(location1, originalPrice.getPickup());
        assertEquals(location2, originalPrice.getDestination());
        assertEquals(TransportOption.INTERNAL_VAN, originalPrice.getTransportOption());
        assertEquals(100.0, originalPrice.getPrice());

        // Second saved price (reverse direction)
        LocationPrice reversePrice = savedPrices.get(1);
        assertEquals(location2, reversePrice.getPickup());
        assertEquals(location1, reversePrice.getDestination());
        assertEquals(TransportOption.INTERNAL_VAN, reversePrice.getTransportOption());
        assertEquals(100.0, reversePrice.getPrice());
    }
}
