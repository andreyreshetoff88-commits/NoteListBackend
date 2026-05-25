package ru.reshetoff.notelistbackend.domain.repository;

import ru.reshetoff.notelistbackend.domain.entity.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository {
    VerificationToken save(VerificationToken token);

    Optional<VerificationToken> findByToken(String token);

    void delete(VerificationToken token);
}
