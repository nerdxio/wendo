package app.wendo.users.services;

import app.wendo.car.Car;
import app.wendo.car.CarRepository;
import app.wendo.car.CarType;
import app.wendo.exceptions.IncompleteRegistrationException;
import app.wendo.exceptions.InvalidRefreshTokenException;
import app.wendo.exceptions.UserNotFoundException;
import app.wendo.files.FilesService;
import app.wendo.security.JwtService;
import app.wendo.users.dtos.AuthenticationRequest;
import app.wendo.users.dtos.AuthenticationResponse;
import app.wendo.users.dtos.RegisterRequest;
import app.wendo.users.models.*;
import app.wendo.users.repositories.DriverRepository;
import app.wendo.users.repositories.PassengerRepository;
import app.wendo.users.repositories.TokenRepository;
import app.wendo.users.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final FilesService filesService;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final TokenRepository tokenRepository;

    public AuthenticationResponse register(RegisterRequest request, Role role) {

        var user = User.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .registrationStatus(RegistrationStatus.STEP_1_COMPLETE)
                .build();

        var savedUser = repository.save(user);

        if (role == Role.PASSENGER) {
            var passenger = Passenger.builder()
                    .user(savedUser)
                    .build();
            passengerRepository.save(passenger);
        }

        if (role == Role.DRIVER) {
            var driver = Driver.builder()
                    .user(savedUser)
                    .isAvailable(true)
                    .build();
            driverRepository.save(driver);
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .registrationStatus(user.getRegistrationStatus().name())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhoneNumber(),
                        request.getPassword()
                )
        );
        var user = repository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(UserNotFoundException::new);

        // Check if registration is complete
        if (user.getRegistrationStatus() != RegistrationStatus.REGISTRATION_COMPLETE) {
            throw new IncompleteRegistrationException("Registration incomplete", user.getRegistrationStatus(), user.getRole().name());
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .registrationStatus(user.getRegistrationStatus().name())
                .build();
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken, userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid refresh token");
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        var user = this.repository.findByPhoneNumber(userEmail)
                .orElseThrow(UserNotFoundException::new);

        try {
            jwtService.isTokenValid(refreshToken, user);
        } catch (Exception e) {
            throw new InvalidRefreshTokenException();
        }

        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    public void completeRegistrationPassengerDocuments(
            String email,
            MultipartFile profilePicture,
            MultipartFile idFrontPicture,
            MultipartFile idBackPicture
    ) {

        var user = getCurrentUser();
        var image = filesService.uploadImage(profilePicture, ImageType.PROFILE_PICTURE, user);

        var nationalIdFront = filesService.uploadImage(idFrontPicture, ImageType.NATIONAL_ID, user);
        var nationalIdBack = filesService.uploadImage(idBackPicture, ImageType.NATIONAL_ID, user);

        var pass = passengerRepository.findByUser(user).orElseThrow(UserNotFoundException::new);
        pass.setNationalIdBack(nationalIdBack.getImageUrl());
        pass.setNationalIdFront(nationalIdFront.getImageUrl());
        passengerRepository.save(pass);

        user.setEmail(email);
        user.setProfileImageUrl(image.getImageUrl());
        user.setRegistrationStatus(RegistrationStatus.REGISTRATION_COMPLETE);
        repository.save(user);
    }

    public void completeRegistrationDriverDocuments(
            String email,
            MultipartFile profilePicture,
            MultipartFile idFrontPicture,
            MultipartFile idBackPicture,
            MultipartFile userLicenseFront,
            MultipartFile userLicenseBack,
            String dataOfBrith
    ) {
        var user = getCurrentUser();
        var image = filesService.uploadImage(profilePicture, ImageType.PROFILE_PICTURE, user);

        var idFront = filesService.uploadImage(idFrontPicture, ImageType.NATIONAL_ID, user);
        var idBack = filesService.uploadImage(idBackPicture, ImageType.NATIONAL_ID, user);
        var licenseFront = filesService.uploadImage(userLicenseFront, ImageType.DRIVER_LICENSE_FRONT, user);
        var licenseBack = filesService.uploadImage(userLicenseBack, ImageType.DRIVER_LICENSE_BACK, user);

        user.setEmail(email);
        user.setProfileImageUrl(image.getImageUrl());
        user.setDateOfBirth(dataOfBrith);
        user.setRegistrationStatus(RegistrationStatus.DRIVER_DOCS_COMPLETE);

        var driver = driverRepository.findByUser(user).orElseThrow();
        driver.setNationalIdBack(idBack.getImageUrl());
        driver.setNationalIdFront(idFront.getImageUrl());
        driver.setDriverLicenseBackPicture(licenseBack.getImageUrl());
        driver.setDriverLicenseFrontPicture(licenseFront.getImageUrl());
        driverRepository.save(driver);

        repository.save(user);
    }


    private User getCurrentUser() {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void completeRegistrationCarInfo(
            MultipartFile image1,
            MultipartFile image2,
            MultipartFile image3,
            MultipartFile image4,
            String carType,
            String licensePlate,
            MultipartFile carLicenseImageFront,
            MultipartFile carLicenseImageBack
    ) {
        var user = getCurrentUser();
        var car = Car.builder()
                .image1(filesService.uploadImage(image1, ImageType.CAR_FRONT, user).getImageUrl())
                .image2(filesService.uploadImage(image2, ImageType.CAR_BACK, user).getImageUrl())
                .image3(filesService.uploadImage(image3, ImageType.CAR_LEFT, user).getImageUrl())
                .image4(filesService.uploadImage(image4, ImageType.CAR_RIGHT, user).getImageUrl())
                .carType(CarType.valueOf(carType))
                .licensePlate(licensePlate)
                .carLicenseImageFront(filesService.uploadImage(carLicenseImageFront, ImageType.CAR_FRONT, user).getImageUrl())
                .carLicenseImageBack(filesService.uploadImage(carLicenseImageBack, ImageType.CAR_BACK, user).getImageUrl())
                .user(user)
                .build();

        var savedCar = carRepository.save(car);
        user.setCar(savedCar);

        // Complete the registration
        user.setRegistrationStatus(RegistrationStatus.REGISTRATION_COMPLETE);
        repository.save(user);
    }

    // Add a method to check registration status
    public RegistrationStatus getRegistrationStatus() {
        return getCurrentUser().getRegistrationStatus();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
