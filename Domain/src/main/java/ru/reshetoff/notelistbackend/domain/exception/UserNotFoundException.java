package ru.reshetoff.notelistbackend.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String identifier, boolean isEmail) {
        super("User with " + (isEmail ? "email" : "identifier") + " " + identifier + " not found");
    }
}
