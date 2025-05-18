package app.wendo.wallet.models;

import app.wendo.users.models.Driver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "wallets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "total_earnings", nullable = false)
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Column(name = "pending_dues", nullable = false)
    private BigDecimal pendingDues = BigDecimal.ZERO;

    @Column(name = "paid_to_platform", nullable = false)
    private BigDecimal paidToPlatform = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Method to add earnings from a trip
    public void addEarnings(BigDecimal tripEarnings, BigDecimal platformCommission) {
        this.totalEarnings = this.totalEarnings.add(tripEarnings);
        this.pendingDues = this.pendingDues.add(platformCommission);
    }

    // Method to record payment to platform
    public void recordPayment(BigDecimal amount) {
        if (amount.compareTo(this.pendingDues) > 0) {
            amount = this.pendingDues; // Cannot pay more than pending dues
        }
        this.pendingDues = this.pendingDues.subtract(amount);
        this.paidToPlatform = this.paidToPlatform.add(amount);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Wallet wallet = (Wallet) o;
        return id != null && Objects.equals(id, wallet.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}