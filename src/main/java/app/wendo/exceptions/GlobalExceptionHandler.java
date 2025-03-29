package app.wendo.exceptions;

import app.wendo.users.models.RegistrationStatus;
import app.wendo.users.models.User;
import app.wendo.users.repositories.UserRepository;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final UserRepository userRepository;

    public GlobalExceptionHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ===============================
    // === Authentication Handlers ===
    // ===============================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getCode(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getCode(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "invalid-credentials",
                "Invalid username or password"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        // If it's a disabled exception, delegate to the specific handler
        if (ex instanceof DisabledException) {
            return handleDisabledException((DisabledException) ex);
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                "authentication-error",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(OtpException.class)
    public ResponseEntity<ErrorResponse> handleOtpException(OtpException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "otp-error",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncompleteRegistrationException.class)
    public ResponseEntity<ErrorResponseWithDetails> handleIncompleteRegistrationException(IncompleteRegistrationException ex) {
        String errorCode = "registration-incomplete";
        Map<String, Object> details = createRegistrationStatusDetails(
            ex.getRegistrationStatus(), 
            ex.getRole(), 
            "registration-incomplete"
        );
        
        ErrorResponseWithDetails errorResponse = new ErrorResponseWithDetails(
                errorCode,
                ex.getMessage(),
                details
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponseWithDetails> handleDisabledException(DisabledException ex) {
        String username = extractUsernameFromMessage(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        String errorCode = "user-disabled";
        
        if (username != null && !username.isEmpty()) {
            Optional<User> userOpt = userRepository.findByPhoneNumber(username);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                details = createRegistrationStatusDetails(
                    user.getRegistrationStatus(),
                    user.getRole().toString(),
                    errorCode
                );
            } else {
                details.put("message", "User account is disabled. Please complete registration or contact support.");
            }
        } else {
            details.put("message", "User account is disabled. Please complete registration or contact support.");
        }
        
        ErrorResponseWithDetails errorResponse = new ErrorResponseWithDetails(
                errorCode,
                "Account disabled: Registration incomplete",
                details
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // ==============================
    // === Validation Handlers ===
    // ==============================
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "validation-error",
                "Validation failed: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "missing-parameter",
                "Required parameter is missing: " + ex.getParameterName()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "type-mismatch",
                "Parameter type mismatch: " + ex.getName()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // ==============================
    // === Fallback Handler ===
    // ==============================
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "server-error",
                "An unexpected error occurred: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // ==============================
    // === Helper Methods ===
    // ==============================
    
    /**
     * Creates details map based on registration status and role
     */
    private Map<String, Object> createRegistrationStatusDetails(RegistrationStatus status, String role, String defaultErrorCode) {
        Map<String, Object> details = new HashMap<>();
        String errorCode = defaultErrorCode;
        
        if (status == null) {
            details.put("nextStep", "unknown");
            return details;
        }
        
        details.put("registrationStatus", status.toString());
        details.put("role", role);
        
        switch (status) {
            case STEP_1_COMPLETE:
                errorCode = "registration-incomplete-basic";
                details.put("nextStep", "complete-profile");
                details.put("requiredDocuments", role != null && role.equals("DRIVER") ? 
                        "ID and driver license documents" : "ID documents");
                details.put("message", "You have only completed basic registration with your phone number");
                break;
            case PASSENGER_COMPLETE:
                if (role != null && role.equals("DRIVER")) {
                    errorCode = "registration-incomplete-driver-docs";
                    details.put("nextStep", "driver-documents");
                    details.put("requiredDocuments", "Driver license documents");
                    details.put("message", "You need to submit your driver license documents");
                } else {
                    // For passengers, this should actually be complete
                    errorCode = "registration-complete";
                    details.put("nextStep", "none");
                    details.put("message", "Your passenger registration is complete");
                }
                break;
            case DRIVER_DOCS_COMPLETE:
                errorCode = "registration-incomplete-car-info";
                details.put("nextStep", "car-info");
                details.put("requiredInfo", "Car images, type, and license plate");
                details.put("message", "You need to submit your car information");
                break;
            case REGISTRATION_COMPLETE:
                // This shouldn't normally be thrown as an exception since it's complete,
                // but we handle it just in case
                errorCode = "registration-complete";
                details.put("nextStep", "none");
                details.put("message", "Your registration is complete");
                break;
            default:
                details.put("nextStep", "unknown");
                details.put("message", "Your account is disabled. Please contact support.");
        }
        
        details.put("errorCode", errorCode);
        return details;
    }
    
    /**
     * Extract username from authentication error message
     * Usually in format "User account is disabled: [username]"
     */
    private String extractUsernameFromMessage(String message) {
        if (message == null) {
            return null;
        }
        
        // Check if the message contains user information
        if (message.contains("User is disabled")) {
            // For multi-part messages like "User is disabled: user@example.com"
            if (message.contains(":")) {
                return message.split(":")[1].trim();
            }
        }
        
        // Try to find username in the message format
        if (message.contains("@")) {
            String[] parts = message.split("\\s+");
            for (String part : parts) {
                if (part.contains("@")) {
                    return part.trim();
                }
            }
        }
        
        return null;
    }
    
    // Inner class for detailed error responses
    public static record ErrorResponseWithDetails(String errorCode, String message, Map<String, Object> details) {
    }
}
