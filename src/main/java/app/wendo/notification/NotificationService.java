package app.wendo.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public void sendOtp(String identifier, String otpCode) {
        if (isEmail(identifier)) {
            sendOtpViaEmail(identifier, otpCode);
        } else {
            sendOtpViaSms(identifier, otpCode);
        }
    }


    private boolean isEmail(String identifier) {
        return EMAIL_PATTERN.matcher(identifier).matches();
    }

    private void sendOtpViaEmail(String email, String otpCode) {
//        emailService.sendOtpEmail(email, otpCode);
    }


    private void sendOtpViaSms(String phoneNumber, String otpCode) {
        // Implement SMS sending logic here
        // For now, we'll just log the OTP
        System.out.println("Sending OTP " + otpCode + " to phone number: " + phoneNumber);
        
        // In a real-world application, you would use an SMS gateway service like Twilio, Nexmo, etc.
        // Example with Twilio (pseudo-code):
        // twilioService.sendSms(phoneNumber, "Your Wendo verification code is: " + otpCode);
    }
}
