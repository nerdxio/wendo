package app.wendo.wallet.repositories;

import app.wendo.users.models.Driver;
import app.wendo.wallet.models.PaymentRequest;
import app.wendo.wallet.models.PaymentRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {
    List<PaymentRequest> findByDriver(Driver driver);
    List<PaymentRequest> findByDriverId(Long driverId);
    List<PaymentRequest> findByStatus(PaymentRequestStatus status);
    Page<PaymentRequest> findByStatus(PaymentRequestStatus status, Pageable pageable);
    List<PaymentRequest> findByDriverAndStatus(Driver driver, PaymentRequestStatus status);
    List<PaymentRequest> findByDriverIdAndStatus(Long driverId, PaymentRequestStatus status);
    List<PaymentRequest> findByCreatedAtBetween(Instant start, Instant end);
    Page<PaymentRequest> findByDriverAndCreatedAtBetween(Driver driver, Instant start, Instant end, Pageable pageable);
}