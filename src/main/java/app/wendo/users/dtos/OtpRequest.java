package app.wendo.users.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record OtpRequest(
        @NotBlank(message = "Identifier (email or phone number) is required")
        @NotBlank String identifier,
        @NotBlank String channel
) {
}
