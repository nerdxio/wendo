package app.wendo.wallet.repositories;

import app.wendo.users.models.Driver;
import app.wendo.wallet.models.Transaction;
import app.wendo.wallet.models.TransactionType;
import app.wendo.wallet.models.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByDriver(Driver driver);
    List<Transaction> findByDriverId(Long driverId);
    List<Transaction> findByWallet(Wallet wallet);
    List<Transaction> findByType(TransactionType type);
    List<Transaction> findByDriverAndType(Driver driver, TransactionType type);
    List<Transaction> findByWalletAndType(Wallet wallet, TransactionType type);
    List<Transaction> findByCreatedAtBetween(Instant start, Instant end);
    Page<Transaction> findByDriverAndCreatedAtBetween(Driver driver, Instant start, Instant end, Pageable pageable);
    Page<Transaction> findByDriverAndType(Driver driver, TransactionType type, Pageable pageable);
    Page<Transaction> findByDriver(Driver driver, Pageable pageable);
}
