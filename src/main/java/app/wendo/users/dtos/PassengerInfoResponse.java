package app.wendo.users.dtos;

public record PassengerInfoResponse(
        Long id,
        String name,
        String phoneNumber,
        String email,
        String profilePicture,
        boolean isOnline,
        String registrationStatus,
        Integer totalTrips,
        boolean hasActiveTrip,
        String nationalIdImage1,
        String nationalIdImage2
) {
}

