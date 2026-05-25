package ru.reshetoff.notelistbackend.domain.service;

public interface VerificationService {
    void sendVerificationEmail(String email);
    void verifyEmail(String token);
}
