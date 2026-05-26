package ru.reshetoff.notelistbackend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String displayName;
    private String email;
    private String phoneNumber;
    private boolean isVerified;
}
