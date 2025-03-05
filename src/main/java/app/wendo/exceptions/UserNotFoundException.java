package app.wendo.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final String code = "user-not-found";
    private final String message = "User not found";

    public UserNotFoundException() {
        super("User not found");
    }
}
