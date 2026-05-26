package ru.reshetoff.notelistbackend.domain.service;

import ru.reshetoff.notelistbackend.domain.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User registerUser(User user);

    User findByEmail(String email);

    User findById(UUID id);

    User findByPhoneNumber(String phoneNumber);

    User findByRefreshToken(String refreshToken);

    List<User> findAllByPhoneNumbers(List<String> phoneNumbers);

    void updateRefreshToken(User user);
}
