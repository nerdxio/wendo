package app.wendo.users.services;

import app.wendo.security.SecurityUtils;
import app.wendo.users.dtos.UserInfoResponse;
import app.wendo.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public UserInfoResponse getUserInfo() {
        var user = securityUtils.getCurrentUser();
        return UserInfoResponse.builder()
                .name(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .profilePicture(user.getProfileImageUrl())
                .isOnline(user.isOnline())
                .build();
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

}
