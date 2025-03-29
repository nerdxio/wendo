package app.wendo.notification;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

//    private final JavaMailSender mailSender;
//
//    public void sendSimpleEmail(String to, String subject, String text) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        mailSender.send(message);
//    }
//
//    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(htmlContent, true);
//
//        mailSender.send(message);
//    }
//
//    public void sendOtpEmail(String to, String otpCode) {
//        try {
//            String subject = "Your Wendo Verification Code";
//            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>"
//                    + "<h2 style='color: #4a4a4a;'>Wendo Verification Code</h2>"
//                    + "<p>Your verification code is:</p>"
//                    + "<h1 style='font-size: 32px; letter-spacing: 5px; background-color: #f5f5f5; padding: 15px; text-align: center; border-radius: 5px;'>"
//                    + otpCode
//                    + "</h1>"
//                    + "<p>This code will expire in 5 minutes.</p>"
//                    + "<p>If you did not request this code, please ignore this email.</p>"
//                    + "<p>Thank you,<br>The Wendo Team</p>"
//                    + "</div>";
//
//            sendHtmlEmail(to, subject, htmlContent);
//        } catch (MessagingException e) {
//            // Fallback to simple email if HTML email fails
//            String subject = "Your Wendo Verification Code";
//            String text = "Your verification code is: " + otpCode + "\n\n"
//                    + "This code will expire in 5 minutes.\n\n"
//                    + "If you did not request this code, please ignore this email.\n\n"
//                    + "Thank you,\nThe Wendo Team";
//
//            sendSimpleEmail(to, subject, text);
//        }
//    }
}
