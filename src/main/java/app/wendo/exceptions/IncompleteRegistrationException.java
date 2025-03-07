package app.wendo.exceptions;

import app.wendo.users.models.RegistrationStatus;
import lombok.Getter;

@Getter
public class IncompleteRegistrationException extends RuntimeException {
    private final RegistrationStatus currentStatus;
    
    public IncompleteRegistrationException(String message, RegistrationStatus currentStatus) {
        super(message);
        this.currentStatus = currentStatus;
    }
}