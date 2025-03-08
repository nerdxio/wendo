package app.wendo.users.models;

import app.wendo.trip.models.Trip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "passengers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_trips")
    private Integer totalTrips;
    
    // Replace one-to-many with many-to-many relationship
    @ManyToMany(mappedBy = "passengers")
    private Set<Trip> trips = new HashSet<>();
    
    // Method to check if passenger has active trips
    public boolean hasActiveTrip() {
        return trips.stream().anyMatch(trip -> 
            trip.getStatus() == app.wendo.trip.models.TripStatus.CONFIRMED ||
            trip.getStatus() == app.wendo.trip.models.TripStatus.IN_PROGRESS
        );
    }
    
    // Method to get active trip if any
    public Trip getActiveTrip() {
        return trips.stream().filter(trip -> 
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
        Passenger passenger = (Passenger) o;
        return id != null && Objects.equals(id, passenger.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
