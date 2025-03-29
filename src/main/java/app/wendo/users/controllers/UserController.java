package app.wendo.users.controllers;

import app.wendo.security.SecurityUtils;
import app.wendo.users.dtos.UserInfoResponse;
import app.wendo.users.models.Role;
import app.wendo.users.services.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        var user = securityUtils.getCurrentUser();
        if (user.getRole() == Role.DRIVER) {
            return ResponseEntity.ok(userService.getDriverInfo());
        } else {
            return ResponseEntity.ok(userService.getPassengerInfo());
        }
    }

    @PostMapping("/status")
    public ResponseEntity<UpdateUserStatusResponse> updateUserStatus(@NotNull @RequestParam boolean online) {
        userService.setUserOnline(online);
        return ResponseEntity.ok(new UpdateUserStatusResponse(true));
    }

    @GetMapping("/status")
    public ResponseEntity<UserStatusResponse> getUserStatus() {
        boolean isOnline = userService.isUserOnline();
        return ResponseEntity.ok(new UserStatusResponse(isOnline));
    }


    public record UserStatusResponse(boolean online) {
    }

    public record UpdateUserStatusResponse(boolean online) {
    }
}
