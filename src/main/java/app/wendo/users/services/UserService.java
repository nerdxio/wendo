package app.wendo.users.services;

import app.wendo.security.SecurityUtils;
import app.wendo.users.dtos.DriverInfoResponse;
import app.wendo.users.dtos.DriverProfileUpdateRequest;
import app.wendo.users.dtos.PassengerInfoResponse;
import app.wendo.users.dtos.PassengerProfileUpdateRequest;
import app.wendo.users.dtos.UserInfoResponse;
import app.wendo.users.models.RegistrationStatus;
import app.wendo.users.repositories.DriverRepository;
import app.wendo.users.repositories.PassengerRepository;
import app.wendo.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;

    public DriverInfoResponse getDriverInfo() {
        var user = securityUtils.getCurrentUser();
        var driver = driverRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        var car = user.getCar();
        var carDTO = car != null ? new DriverInfoResponse.CarDTO(
                car.getImage1(),
                car.getImage2(),
                car.getImage3(),
                car.getImage4(),
                car.getCarType().name(),
                car.getLicensePlate(),
                car.getCarLicenseImageFront(),
                car.getCarLicenseImageBack()
        ) : null;

        return new DriverInfoResponse(
                user.getId(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.isOnline(),
                user.getRegistrationStatus().name(),
                driver.getIsAvailable(),
                driver.getRating(),
                driver.getTotalTrips(),
                driver.hasActiveTrip(),
                driver.getNationalIdBack(),
                driver.getNationalIdFront(),
                driver.getDriverLicenseBackPicture(),
                driver.getDriverLicenseFrontPicture(),
                carDTO
        );
    }


    public PassengerInfoResponse getPassengerInfo() {
        var user = securityUtils.getCurrentUser();
        var passenger = passengerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        return new PassengerInfoResponse(
                user.getId(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.isOnline(),
                user.getRegistrationStatus().name(),
                passenger.getTotalTrips(),
                passenger.hasActiveTrip(),
                passenger.getNationalIdBack(),
                passenger.getNationalIdFront()
        );
    }

    public void setUserOnline(boolean online) {
        var user = securityUtils.getCurrentUser();
        user.setOnline(online);
        userRepository.save(user);
    }

    public boolean isUserOnline() {
        var user = securityUtils.getCurrentUser();
        return user.isOnline();
    }

    public RegistrationStatus getRegistrationStatus() {
        var user = securityUtils.getCurrentUser();
        return user.getRegistrationStatus();
    }

    public boolean isRegistrationComplete() {
        return getRegistrationStatus() == RegistrationStatus.REGISTRATION_COMPLETE;
    }

    /**
     * Updates the driver profile with the provided information
     * @param request The profile update request
     * @return Updated driver information
     */
    public DriverInfoResponse updateDriverProfile(DriverProfileUpdateRequest request) {
        var user = securityUtils.getCurrentUser();

        // Update user fields if provided
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        // Save the updated user
        userRepository.save(user);

        // Return the updated driver info
        return getDriverInfo();
    }

    /**
     * Updates the passenger profile with the provided information
     * @param request The profile update request
     * @return Updated passenger information
     */
    public PassengerInfoResponse updatePassengerProfile(PassengerProfileUpdateRequest request) {
        var user = securityUtils.getCurrentUser();

        // Update user fields if provided
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        // Save the updated user
        userRepository.save(user);

        // Return the updated passenger info
        return getPassengerInfo();
    }
}
