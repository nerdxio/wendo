package app.wendo.exceptions;

import lombok.Getter;

@Getter
public class InvalidRefreshTokenException extends RuntimeException {

    private final String code = "invalid-refresh-token";
    private final String message = "Invalid refresh token";

    public InvalidRefreshTokenException() {
        super("Invalid refresh token");
    }

}
