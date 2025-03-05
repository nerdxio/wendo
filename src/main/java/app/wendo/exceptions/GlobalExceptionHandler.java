package app.wendo.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
