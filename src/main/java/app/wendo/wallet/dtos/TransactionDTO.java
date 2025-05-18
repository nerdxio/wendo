package app.wendo.wallet.dtos;

import app.wendo.wallet.models.Transaction;
import app.wendo.wallet.models.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long driverId;
    private String driverName;
    private Long walletId;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private Long tripId;
    private Long paymentRequestId;
    private Instant createdAt;

    public static TransactionDTO fromEntity(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .driverId(transaction.getDriver().getId())
                .driverName(transaction.getDriver().getUser().getFullName())
                .walletId(transaction.getWallet().getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .tripId(transaction.getTrip() != null ? transaction.getTrip().getId() : null)
                .paymentRequestId(transaction.getPaymentRequest() != null ? transaction.getPaymentRequest().getId() : null)
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}