package app.wendo.wallet.services;

import app.wendo.trip.models.Trip;
import app.wendo.users.models.Driver;
import app.wendo.users.repositories.DriverRepository;
import app.wendo.wallet.models.*;
import app.wendo.wallet.repositories.PaymentRequestRepository;
import app.wendo.wallet.repositories.TransactionRepository;
import app.wendo.wallet.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final DriverRepository driverRepository;

    // Platform commission percentage (e.g., 20%)
    private static final BigDecimal PLATFORM_COMMISSION_PERCENTAGE = new BigDecimal("0.20");

    @Transactional
    public Wallet getOrCreateWallet(Driver driver) {
        return walletRepository.findByDriver(driver)
                .orElseGet(() -> {
                    Wallet wallet = Wallet.builder()
                            .driver(driver)
                            .totalEarnings(BigDecimal.ZERO)
                            .pendingDues(BigDecimal.ZERO)
                            .paidToPlatform(BigDecimal.ZERO)
                            .build();
                    return walletRepository.save(wallet);
                });
    }

    @Transactional
    public Wallet getWalletByDriverId(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with ID: " + driverId));
        return getOrCreateWallet(driver);
    }

    @Transactional
    public void processTrip(Trip trip, BigDecimal tripFare) {
        Driver driver = trip.getDriver();
        Wallet wallet = getOrCreateWallet(driver);

        // Calculate platform commission
        BigDecimal platformCommission = tripFare.multiply(PLATFORM_COMMISSION_PERCENTAGE);

        // Calculate driver earnings
        BigDecimal driverEarnings = tripFare.subtract(platformCommission);

        // Update wallet
        wallet.addEarnings(driverEarnings, platformCommission);
        walletRepository.save(wallet);

        // Record driver earnings transaction
        Transaction earningsTransaction = Transaction.builder()
                .driver(driver)
                .wallet(wallet)
                .type(TransactionType.TRIP_EARNING)
                .amount(driverEarnings)
                .description("Earnings from trip #" + trip.getId())
                .trip(trip)
                .build();
        transactionRepository.save(earningsTransaction);

        // Record platform commission transaction
        Transaction commissionTransaction = Transaction.builder()
                .driver(driver)
                .wallet(wallet)
                .type(TransactionType.PLATFORM_COMMISSION)
                .amount(platformCommission)
                .description("Platform commission for trip #" + trip.getId())
                .trip(trip)
                .build();
        transactionRepository.save(commissionTransaction);
    }

    @Transactional
    public PaymentRequest createPaymentRequest(Long driverId, BigDecimal amount, String notes) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with ID: " + driverId));

        Wallet wallet = getOrCreateWallet(driver);

        // Ensure amount is not greater than pending dues
        if (amount.compareTo(wallet.getPendingDues()) > 0) {
            amount = wallet.getPendingDues();
        }

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .driver(driver)
                .amount(amount)
                .notes(notes)
                .status(PaymentRequestStatus.PENDING)
                .build();

        return paymentRequestRepository.save(paymentRequest);
    }

    @Transactional
    public PaymentRequest processPaymentRequest(Long paymentRequestId, PaymentRequestStatus newStatus) {
        PaymentRequest paymentRequest = paymentRequestRepository.findById(paymentRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Payment request not found with ID: " + paymentRequestId));

        if (paymentRequest.getStatus() != PaymentRequestStatus.PENDING) {
            throw new IllegalStateException("Payment request is not in PENDING state");
        }

        if (newStatus == PaymentRequestStatus.PAID) {
            paymentRequest.markAsPaid();

            // Update wallet
            Wallet wallet = getOrCreateWallet(paymentRequest.getDriver());
            wallet.recordPayment(paymentRequest.getAmount());
            walletRepository.save(wallet);

            // Record transaction
            Transaction transaction = Transaction.builder()
                    .driver(paymentRequest.getDriver())
                    .wallet(wallet)
                    .type(TransactionType.PAYMENT_TO_PLATFORM)
                    .amount(paymentRequest.getAmount())
                    .description("Payment to platform")
                    .paymentRequest(paymentRequest)
                    .build();
            transactionRepository.save(transaction);
        } else if (newStatus == PaymentRequestStatus.REJECTED) {
            paymentRequest.reject();
        }

        return paymentRequestRepository.save(paymentRequest);
    }

    public List<PaymentRequest> getDriverPaymentRequests(Long driverId) {
        return paymentRequestRepository.findByDriverId(driverId);
    }

    public List<PaymentRequest> getPendingPaymentRequests() {
        return paymentRequestRepository.findByStatus(PaymentRequestStatus.PENDING);
    }

    public Page<PaymentRequest> getPendingPaymentRequests(Pageable pageable) {
        return paymentRequestRepository.findByStatus(PaymentRequestStatus.PENDING, pageable);
    }

    public Page<PaymentRequest> getPaidPaymentRequests(Pageable pageable) {
        return paymentRequestRepository.findByStatus(PaymentRequestStatus.PAID, pageable);
    }

    public List<Transaction> getDriverTransactions(Long driverId) {
        return transactionRepository.findByDriverId(driverId);
    }

    public Page<Transaction> getDriverTransactions(Long driverId, Pageable pageable) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with ID: " + driverId));
        return transactionRepository.findByDriver(driver, pageable);
    }

    public Page<Transaction> getDriverTransactionsByType(Long driverId, TransactionType type, Pageable pageable) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with ID: " + driverId));
        return transactionRepository.findByDriverAndType(driver, type, pageable);
    }

    public Page<Transaction> getDriverTransactionsByDateRange(Long driverId, Instant start, Instant end, Pageable pageable) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with ID: " + driverId));
        return transactionRepository.findByDriverAndCreatedAtBetween(driver, start, end, pageable);
    }

    public List<Driver> getDriversWithPendingDues() {
        // This is a simplified implementation. In a real-world scenario, you might want to use a custom query
        List<Wallet> walletsWithDues = walletRepository.findAll().stream()
                .filter(wallet -> wallet.getPendingDues().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        return walletsWithDues.stream()
                .map(Wallet::getDriver)
                .toList();
    }

    @Transactional
    public List<PaymentRequest> createPaymentRequestsForAllDrivers(String notes) {
        List<Driver> driversWithDues = getDriversWithPendingDues();
        return driversWithDues.stream()
                .map(driver -> {
                    Wallet wallet = getOrCreateWallet(driver);
                    return createPaymentRequest(driver.getId(), wallet.getPendingDues(), notes);
                })
                .toList();
    }
}
