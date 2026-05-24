package ru.reshetoff.notelistbackend.domain.repository;

import ru.reshetoff.notelistbackend.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User>findById(UUID id);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByRefreshToken(String refreshToken);

    boolean existsByEmail(String email);
}
