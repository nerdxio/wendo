package app.wendo.exceptions;

import app.wendo.users.models.RegistrationStatus;
import lombok.Getter;

@Getter
public class IncompleteRegistrationException extends RuntimeException {
    
    private final RegistrationStatus registrationStatus;
    private final String role;
    
    public IncompleteRegistrationException(String message) {
        super(message);
        this.registrationStatus = null;
        this.role = null;
    }
    
    public IncompleteRegistrationException(String message, RegistrationStatus registrationStatus, String role) {
        super(message);
        this.registrationStatus = registrationStatus;
        this.role = role;
    }
    
    public String getCode() {
        return "registration-incomplete";
    }

}