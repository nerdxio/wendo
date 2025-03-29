package app.wendo.users.controllers;

import app.wendo.exceptions.OtpException;
import app.wendo.users.dtos.OtpRequest;
import app.wendo.users.dtos.OtpResponse;
import app.wendo.users.dtos.OtpVerificationRequest;
import app.wendo.users.services.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp/auth")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<OtpResponse> generateOtp(@Valid @RequestBody OtpRequest request) {
        try {
            String otpCode = otpService.generateOtp(request);

            return ResponseEntity.ok(OtpResponse.builder()
                    .message("OTP generated successfully: " + otpCode)
                    .identifier(request.identifier())
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(OtpResponse.builder()
                    .message("Failed to generate OTP: " + e.getMessage())
                    .identifier(request.identifier())
                    .success(false)
                    .build());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<OtpResponse> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        try {
            boolean verified = otpService.verifyOtp(request.getIdentifier(), request.getOtpCode());
            
            return ResponseEntity.ok(OtpResponse.builder()
                    .message("OTP verified successfully")
                    .identifier(request.getIdentifier())
                    .success(verified)
                    .build());
        } catch (OtpException e) {
            return ResponseEntity.badRequest().body(OtpResponse.builder()
                    .message("OTP verification failed: " + e.getMessage())
                    .identifier(request.getIdentifier())
                    .success(false)
                    .build());
        }
    }
}
