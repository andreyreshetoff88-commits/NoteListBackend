package ru.reshetoff.notelistbackend.domain.service;

public interface VerificationService {
    void sendVerificationCode(String email, String testToken);
    void verifyCode(String email, String code);
}
