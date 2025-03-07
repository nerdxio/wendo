package app.wendo.exceptions;

import java.util.Map;

public record ErrorResponseWithDetails(
    String errorCode,
    String message,
    Map<String, Object> details
) {
}
