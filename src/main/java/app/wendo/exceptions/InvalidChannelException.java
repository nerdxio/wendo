package app.wendo.exceptions;

public class InvalidChannelException extends RuntimeException {
    public InvalidChannelException(String message) {
        super(message);
    }
}