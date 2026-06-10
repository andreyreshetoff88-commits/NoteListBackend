package ru.reshetoff.notelistbackend.domain.repository;

import ru.reshetoff.notelistbackend.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByRefreshToken(String refreshToken);

    List<User> findUnverifiedOlderThan(LocalDateTime threshold);

    boolean existsByEmail(String email);

    void delete(User user);
}
