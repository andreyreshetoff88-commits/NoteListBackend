package ru.reshetoff.notelistbackend.domain.exception;

public class InvalidVerificationTokenException extends RuntimeException {
    public InvalidVerificationTokenException(String token) {
        super("Invalid or expired verification token: " + token);
    }
}
