package ru.reshetoff.notelistbackend.domain.service;

import ru.reshetoff.notelistbackend.domain.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(User user);

    User findByEmail(String email);

    User findByPhoneNumber(String phoneNumber);

    User findByRefreshToken(String refreshToken);

    List<User> findAllByPhoneNumbers(List<String> phoneNumbers);

    void updateRefreshToken(User user);
}
