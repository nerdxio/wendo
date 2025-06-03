package app.wendo.users.controllers;

import app.wendo.security.SecurityUtils;
import app.wendo.users.dtos.DriverInfoResponse;
import app.wendo.users.dtos.DriverProfileUpdateRequest;
import app.wendo.users.dtos.PassengerInfoResponse;
import app.wendo.users.dtos.PassengerProfileUpdateRequest;
import app.wendo.users.dtos.UserInfoResponse;
import app.wendo.users.models.Role;
import app.wendo.users.services.UserService;
import jakarta.validation.Valid;
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

    /**
     * Updates the driver profile with the provided information
     * @param request The profile update request
     * @return Updated driver information
     */
    @PutMapping("/driver/profile")
    public ResponseEntity<DriverInfoResponse> updateDriverProfile(@Valid @RequestBody DriverProfileUpdateRequest request) {
        var user = securityUtils.getCurrentUser();
        if (user.getRole() != Role.DRIVER) {
            return ResponseEntity.badRequest().build();
        }

        DriverInfoResponse updatedProfile = userService.updateDriverProfile(request);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Updates the passenger profile with the provided information
     * @param request The profile update request
     * @return Updated passenger information
     */
    @PutMapping("/passenger/profile")
    public ResponseEntity<PassengerInfoResponse> updatePassengerProfile(@Valid @RequestBody PassengerProfileUpdateRequest request) {
        var user = securityUtils.getCurrentUser();
        if (user.getRole() != Role.PASSENGER) {
            return ResponseEntity.badRequest().build();
        }

        PassengerInfoResponse updatedProfile = userService.updatePassengerProfile(request);
        return ResponseEntity.ok(updatedProfile);
    }
}
