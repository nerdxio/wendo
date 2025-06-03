package app.wendo.wallet.controllers;

import app.wendo.users.models.Driver;
import app.wendo.wallet.dtos.PaymentRequestDTO;
import app.wendo.wallet.dtos.WalletDTO;
import app.wendo.wallet.models.PaymentRequest;
import app.wendo.wallet.models.PaymentRequestStatus;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/wallet")
@RequiredArgsConstructor
public class AdminWalletController {
    private final WalletService walletService;

    @GetMapping("/drivers/{driverId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletDTO> getDriverWallet(@PathVariable Long driverId) {
        Wallet wallet = walletService.getWalletByDriverId(driverId);
        return ResponseEntity.ok(WalletDTO.fromEntity(wallet));
    }

    @GetMapping("/payment-requests/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentRequestDTO>> getPendingPaymentRequests() {
        List<PaymentRequest> paymentRequests = walletService.getPendingPaymentRequests();
        List<PaymentRequestDTO> paymentRequestDTOs = paymentRequests.stream()
                .map(PaymentRequestDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(paymentRequestDTOs);
    }

    @GetMapping("/payment-requests/pending/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentRequestDTO>> getPendingPaymentRequestsPaged(Pageable pageable) {
        Page<PaymentRequest> paymentRequests = walletService.getPendingPaymentRequests(pageable);
        Page<PaymentRequestDTO> paymentRequestDTOs = paymentRequests.map(PaymentRequestDTO::fromEntity);

        return ResponseEntity.ok(paymentRequestDTOs);
    }

    @GetMapping("/payment-requests/paid/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentRequestDTO>> getPaidPaymentRequestsPaged(Pageable pageable) {
        Page<PaymentRequest> paymentRequests = walletService.getPaidPaymentRequests(pageable);
        Page<PaymentRequestDTO> paymentRequestDTOs = paymentRequests.map(PaymentRequestDTO::fromEntity);

        return ResponseEntity.ok(paymentRequestDTOs);
    }

    @PutMapping("/payment-requests/{paymentRequestId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentRequestDTO> updatePaymentRequestStatus(
            @PathVariable Long paymentRequestId,
            @RequestParam PaymentRequestStatus status) {
        PaymentRequest paymentRequest = walletService.processPaymentRequest(paymentRequestId, status);
        return ResponseEntity.ok(PaymentRequestDTO.fromEntity(paymentRequest));
    }

    @GetMapping("/drivers/with-pending-dues")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WalletDTO>> getDriversWithPendingDues() {
        List<Driver> drivers = walletService.getDriversWithPendingDues();
        List<WalletDTO> walletDTOs = drivers.stream()
                .map(driver -> {
                    Wallet wallet = walletService.getOrCreateWallet(driver);
                    return WalletDTO.fromEntity(wallet);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(walletDTOs);
    }


    @PostMapping("/payment-requests/create-for-all-drivers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentRequestDTO>> createPaymentRequestsForAllDrivers(
            @RequestParam(required = false) String notes) {
        List<PaymentRequest> paymentRequests = walletService.createPaymentRequestsForAllDrivers(notes);

        List<PaymentRequestDTO> paymentRequestDTOs = paymentRequests.stream()
                .map(PaymentRequestDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(paymentRequestDTOs);
    }
}
