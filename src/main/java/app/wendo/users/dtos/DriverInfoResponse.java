package app.wendo.users.dtos;

import org.springframework.web.multipart.MultipartFile;

public record DriverInfoResponse(
        Long id,
        String name,
        String phoneNumber,
        String email,
        String profilePicture,
        boolean isOnline,
        String registrationStatus,
        Boolean isAvailable,
        Double rating,
        Integer totalTrips,
        boolean hasActiveTrip,
        String idFrontPicture,
        String idBackPicture,
        String userLicenseFront,
        String userLicenseBack,

        CarDTO car
) {
    public record CarDTO(
            String image1,
            String image2,
            String image3,
            String image4,
            String carType,
            String licensePlate,
            String carLicenseImageFront,
            String carLicenseImageBack
    ) {}
}