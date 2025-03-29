package app.wendo.users.models;

import app.wendo.car.Car;
import app.wendo.trip.models.Trip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "drivers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_available")
    private Boolean isAvailable = false;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "total_trips")
    private Integer totalTrips;

    @Column(name = "national_id_front")
    private String nationalIdFront;

    @Column(name = "national_id_back")
    private String nationalIdBack;

    @Column(name = "driver_license_front_picture")
    private String driverLicenseFrontPicture;
    @Column(name = "driver_license_back_picture")
    private String driverLicenseBackPicture;
//    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Car car;
//
    // One driver can have many trips, but can only have one active trip at a time
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Trip> trips = new ArrayList<>();
    
    // Method to check if driver has active trips
    public boolean hasActiveTrip() {
        return trips.stream().anyMatch(trip -> 
            trip.getStatus() == app.wendo.trip.models.TripStatus.CREATED ||
            trip.getStatus() == app.wendo.trip.models.TripStatus.SEARCHING ||
            trip.getStatus() == app.wendo.trip.models.TripStatus.CONFIRMED ||
            trip.getStatus() == app.wendo.trip.models.TripStatus.IN_PROGRESS
        );
    }
    
    // Method to get active trip if any
    public Trip getActiveTrip() {
        return trips.stream().filter(trip -> 
            trip.getStatus() == app.wendo.trip.models.TripStatus.CREATED ||
            trip.getStatus() == app.wendo.trip.models.TripStatus.SEARCHING ||
            trip.getStatus() == app.wendo.trip.models.TripStatus.CONFIRMED ||
            trip.getStatus() == app.wendo.trip.models.TripStatus.IN_PROGRESS
        ).findFirst().orElse(null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Driver driver = (Driver) o;
        return id != null && Objects.equals(id, driver.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}