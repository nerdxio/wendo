package app.wendo.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        return new ErrorResponse(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ErrorResponse handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        return new ErrorResponse(e.getCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(IncompleteRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleIncompleteRegistrationException(IncompleteRegistrationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "registration-incomplete",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

}
