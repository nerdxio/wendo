package app.wendo.wallet.controllers;

import app.wendo.security.SecurityUtils;
import app.wendo.users.models.Driver;
import app.wendo.users.models.User;
import app.wendo.users.repositories.DriverRepository;
import app.wendo.wallet.dtos.PaymentRequestDTO;
import app.wendo.wallet.dtos.TransactionDTO;
import app.wendo.wallet.dtos.WalletDTO;
import app.wendo.wallet.models.PaymentRequest;
import app.wendo.wallet.models.Transaction;
import app.wendo.wallet.models.TransactionType;
import app.wendo.wallet.models.Wallet;
import app.wendo.wallet.services.WalletService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/driver/wallet")
@RequiredArgsConstructor
public class DriverWalletController {
    private final WalletService walletService;
    private final DriverRepository driverRepository;
    private final SecurityUtils securityUtils;

    @GetMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<WalletDTO> getWallet() {
        User currentUser = securityUtils.getCurrentUser();
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found for current user"));

        Wallet wallet = walletService.getOrCreateWallet(driver);
        return ResponseEntity.ok(WalletDTO.fromEntity(wallet));
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<TransactionDTO>> getTransactions() {
        User currentUser = securityUtils.getCurrentUser();
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found for current user"));

        List<Transaction> transactions = walletService.getDriverTransactions(driver.getId());
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(TransactionDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/transactions/paged")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsPaged(Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found for current user"));

        Page<Transaction> transactions = walletService.getDriverTransactions(driver.getId(), pageable);
        Page<TransactionDTO> transactionDTOs = transactions.map(TransactionDTO::fromEntity);

        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/transactions/type/{type}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsByType(
            @PathVariable TransactionType type,
            Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found for current user"));

        Page<Transaction> transactions = walletService.getDriverTransactionsByType(driver.getId(), type, pageable);
        Page<TransactionDTO> transactionDTOs = transactions.map(TransactionDTO::fromEntity);

        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/transactions/date-range")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsByDateRange(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate,
            Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found for current user"));

        Page<Transaction> transactions = walletService.getDriverTransactionsByDateRange(
                driver.getId(), startDate, endDate, pageable);
        Page<TransactionDTO> transactionDTOs = transactions.map(TransactionDTO::fromEntity);

        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/payment-requests")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<PaymentRequestDTO>> getPaymentRequests() {
        User currentUser = securityUtils.getCurrentUser();
        Driver driver = driverRepository.findByUser(currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found for current user"));

        List<PaymentRequest> paymentRequests = walletService.getDriverPaymentRequests(driver.getId());
        List<PaymentRequestDTO> paymentRequestDTOs = paymentRequests.stream()
                .map(PaymentRequestDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(paymentRequestDTOs);
    }

}
