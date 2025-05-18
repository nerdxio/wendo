package app.wendo.wallet.dtos;

import app.wendo.wallet.models.Wallet;
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
public class WalletDTO {
    private Long id;
    private Long driverId;
    private String driverName;
    private BigDecimal totalEarnings;
    private BigDecimal pendingDues;
    private BigDecimal paidToPlatform;
    private Instant createdAt;
    private Instant updatedAt;

    public static WalletDTO fromEntity(Wallet wallet) {
        return WalletDTO.builder()
                .id(wallet.getId())
                .driverId(wallet.getDriver().getId())
                .driverName(wallet.getDriver().getUser().getFullName())
                .totalEarnings(wallet.getTotalEarnings())
                .pendingDues(wallet.getPendingDues())
                .paidToPlatform(wallet.getPaidToPlatform())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}