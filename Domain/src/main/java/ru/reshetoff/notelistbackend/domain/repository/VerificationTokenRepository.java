package ru.reshetoff.notelistbackend.domain.repository;

import ru.reshetoff.notelistbackend.domain.entity.User;
import ru.reshetoff.notelistbackend.domain.entity.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository {
    void save(VerificationToken token);

    Optional<VerificationToken> findByToken(String token);

    void delete(VerificationToken token);

    void deleteByUser(User user);
}
