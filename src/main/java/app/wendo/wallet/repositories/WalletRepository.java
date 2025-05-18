package app.wendo.wallet.repositories;

import app.wendo.users.models.Driver;
import app.wendo.wallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByDriver(Driver driver);
    Optional<Wallet> findByDriverId(Long driverId);
    boolean existsByDriver(Driver driver);
}