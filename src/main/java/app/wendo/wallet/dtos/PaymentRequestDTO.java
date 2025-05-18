package app.wendo.wallet.dtos;

import app.wendo.wallet.models.PaymentRequest;
import app.wendo.wallet.models.PaymentRequestStatus;
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
public class PaymentRequestDTO {
    private Long id;
    private Long driverId;
    private String driverName;
    private BigDecimal amount;
    private String notes;
    private PaymentRequestStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;

    public static PaymentRequestDTO fromEntity(PaymentRequest paymentRequest) {
        return PaymentRequestDTO.builder()
                .id(paymentRequest.getId())
                .driverId(paymentRequest.getDriver().getId())
                .driverName(paymentRequest.getDriver().getUser().getFullName())
                .amount(paymentRequest.getAmount())
                .notes(paymentRequest.getNotes())
                .status(paymentRequest.getStatus())
                .createdAt(paymentRequest.getCreatedAt())
                .updatedAt(paymentRequest.getUpdatedAt())
                .completedAt(paymentRequest.getCompletedAt())
                .build();
    }
}