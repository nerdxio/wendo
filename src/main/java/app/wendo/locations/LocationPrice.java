package app.wendo.locations;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "location_prices")
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Location pickup;

    @ManyToOne
    private Location destination;

    private double price;

    @Enumerated(EnumType.STRING)
    private TransportOption transportOption;
}