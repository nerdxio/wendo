package app.wendo.users.services;

import app.wendo.exceptions.OtpException;
import app.wendo.users.dtos.OtpRequest;
import app.wendo.users.models.Channel;
import app.wendo.users.models.Otp;
import app.wendo.users.repositories.OtpRepository;
import app.wendo.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final NotificationService notificationService;
    private static final int OTP_LENGTH = 4;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateOtp(OtpRequest request) {

        otpRepository.findTopByIdentifierOrderByCreatedAtDesc(request.identifier())
                .ifPresent(existingOtp -> {
                    if (!existingOtp.isExpired() && !existingOtp.isUsed()) {
                        existingOtp.setUsed(true);
                        otpRepository.save(existingOtp);
                    }
                });

        String otpCode = "1234";
//        String otpCode = generateRandomOtp();

        Channel.validate(request.channel());


        Otp otp = Otp.builder()
                .code(otpCode)
                .channel(Channel.fromString(request.channel()))
                .identifier(request.identifier())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .build();

        otpRepository.save(otp);


        try {
            notificationService.sendOtp(request.identifier(), otpCode);
        } catch (Exception e) {
            // Log the error but don't fail the OTP generation
            System.err.println("Failed to send OTP notification: " + e.getMessage());
        }

        return otpCode;
    }

    public boolean verifyOtp(String identifier, String otpCode) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new OtpException("Identifier (email or phone number) cannot be empty");
        }

        if (otpCode == null || otpCode.trim().isEmpty()) {
            throw new OtpException("OTP code cannot be empty");
        }

        Otp otp = otpRepository.findByIdentifierAndCodeAndUsedFalse(identifier, otpCode)
                .orElseThrow(() -> new OtpException("Invalid OTP"));

        if (otp.isExpired()) {
            throw new OtpException("OTP has expired");
        }

        // Mark the OTP as used
        otp.setUsed(true);
        otp.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(otp);

        return true;
    }


    private String generateRandomOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }
}
