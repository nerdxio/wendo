package app.wendo.exceptions;

public record ErrorResponse(
        String errorCode,
        String message
) {
}
