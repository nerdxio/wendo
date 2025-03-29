package app.wendo.users.controllers;

import app.wendo.users.dtos.AuthenticationRequest;
import app.wendo.users.dtos.AuthenticationResponse;
import app.wendo.users.dtos.RegisterRequest;
import app.wendo.users.models.Role;
import app.wendo.users.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeRegistrationDriverDocuments(
            @Email @RequestParam String email,
            @RequestParam("profilePicture") MultipartFile profilePicture,
            @RequestParam("idFrontPicture") MultipartFile idFrontPicture,
            @RequestParam("idBackPicture") MultipartFile idBackPicture,
            @RequestParam("driverLicenseFront") MultipartFile driverLicenseFront,
            @RequestParam("driverLicenseBack") MultipartFile driverLicenseBack,
            @NotBlank @RequestParam("dataOfBrith") String dataOfBrith
    ) {
        service.completeRegistrationDriverDocuments(
                email,
                profilePicture,
                idFrontPicture,
                idBackPicture,
                driverLicenseFront,
                driverLicenseBack,
                dataOfBrith
        );
    }

    @PostMapping("/complete-registration/driver/car")
    public void completeRegistrationCarInfo(
            @RequestParam MultipartFile image1,
            @RequestParam MultipartFile image2,
            @RequestParam MultipartFile image3,
            @RequestParam MultipartFile image4,
            @RequestParam String carType,
            @RequestParam String licensePlate,
            @RequestParam MultipartFile carLicenseImageFront,
            @RequestParam MultipartFile carLicenseImageBack
    ) {

        service.completeRegistrationCarInfo(
                image1,
                image2,
                image3,
                image4,
                carType,
                licensePlate,
                carLicenseImageFront,
                carLicenseImageBack
        );
    }

}
