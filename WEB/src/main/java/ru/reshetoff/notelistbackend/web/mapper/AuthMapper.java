package ru.reshetoff.notelistbackend.web.mapper;

import ru.reshetoff.notelistbackend.domain.entity.User;
import ru.reshetoff.notelistbackend.web.dto.requests.RegisterUserRequest;

public class AuthMapper {
    public static User toEntity(RegisterUserRequest request) {
        User user = new User();

        user.setDisplayName(request.getDisplayName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return user;
    }
}
