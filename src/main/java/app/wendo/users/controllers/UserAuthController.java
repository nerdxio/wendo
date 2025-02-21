package app.wendo.users.controllers;

import app.wendo.files.FilesService;
import app.wendo.users.dtos.AuthenticationRequest;
import app.wendo.users.dtos.AuthenticationResponse;
import app.wendo.users.dtos.RegisterRequest;
import app.wendo.users.models.Role;
import app.wendo.users.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class UserAuthController {

    private final AuthenticationService service;
    private final FilesService filesService;

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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/complete-registration/passenger")
    public void completeRegistrationPassengerDocuments(
            @Email @RequestParam String email,
            @RequestParam("profilePicture") MultipartFile profilePicture,
            @RequestParam("idFrontPicture") MultipartFile idFrontPicture,
            @RequestParam("idBackPicture") MultipartFile idBackPicture
    ) {
        service.completeRegistrationPassengerDocuments(
                email,
                profilePicture,
                idFrontPicture,
                idBackPicture
        );
    }

    @PostMapping("/complete-registration/driver/documents")
    public ResponseEntity<String> completeRegistrationDriverDocuments(
            @RequestParam String email,
//            @RequestParam String cartLineNumber,
//            @RequestParam String carType,
            @RequestParam("profilePicture") MultipartFile profilePicture
//            @RequestParam("frontLicensePicture") MultipartFile frontLicensePicture,
//            @RequestParam("backLicensePicture") MultipartFile backLicensePicture,
//            @RequestParam("carPicture") MultipartFile carPicture
    ) {

        return ResponseEntity.ok(service.completeRegistration(profilePicture));
    }


    @PostMapping("/complete-registration/driver/car-images")
    public ResponseEntity<String> completeRegistrationCarImages(
            @RequestParam("profilePicture") MultipartFile profilePicture
    ) {
        return ResponseEntity.ok(service.completeRegistration(profilePicture));
    }

}
