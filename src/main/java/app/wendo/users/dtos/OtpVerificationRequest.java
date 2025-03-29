package app.wendo.users.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerificationRequest {
    @NotBlank(message = "Identifier (email or phone number) is required")
    private String identifier;
    
    @NotBlank(message = "OTP code is required")
    private String otpCode;
}
