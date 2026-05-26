package ru.reshetoff.notelistbackend.web.mapper;

import ru.reshetoff.notelistbackend.domain.entity.User;
import ru.reshetoff.notelistbackend.web.dto.requests.RegisterUserRequest;
import ru.reshetoff.notelistbackend.web.dto.response.UserResponse;

public class AuthMapper {
    public static User toEntity(RegisterUserRequest request) {
        User user = new User();

        user.setDisplayName(request.getDisplayName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return user;
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.isVerified()
        );
    }
}
