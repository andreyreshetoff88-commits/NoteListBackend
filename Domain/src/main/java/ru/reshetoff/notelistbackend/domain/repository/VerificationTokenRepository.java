package ru.reshetoff.notelistbackend.domain.repository;

import ru.reshetoff.notelistbackend.domain.entity.User;
import ru.reshetoff.notelistbackend.domain.entity.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository {
    void save(VerificationToken token);

    Optional<VerificationToken> findByCode(String code);

    Optional<VerificationToken> findByUserEmail(String email);

    void delete(VerificationToken token);

    void deleteByUser(User user);

    void incrementAttempts(VerificationToken token);
}
