package app.wendo.users.dtos;

import lombok.Builder;

@Builder
public record UserInfoResponse(
        String name,
        String email,
        String profilePicture,
        String phoneNumber,
        boolean isOnline
) {
}