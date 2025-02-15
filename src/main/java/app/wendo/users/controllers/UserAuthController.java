package app.wendo.users.controllers;

import app.wendo.users.dtos.AuthenticationRequest;
import app.wendo.users.dtos.AuthenticationResponse;
import app.wendo.users.dtos.RegisterRequest;
import app.wendo.users.models.Role;
import app.wendo.users.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final AuthenticationService service;

    @PostMapping("/register/passenger")
    public ResponseEntity<AuthenticationResponse> registerAsPassenger(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request, Role.PASSENGER));
    }

    @PostMapping("/register/driver")
    public ResponseEntity<AuthenticationResponse> registerAsDriver(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request, Role.DRIVER));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(service.refreshToken(request));
    }

//    @PostMapping("/complete-registration")
//    public ResponseEntity<AuthenticationResponse> completeRegistration(
//            @RequestBody RegisterRequest request
//    ) {
//        return ResponseEntity.ok(service.completeRegistration(request));
//    }
}
