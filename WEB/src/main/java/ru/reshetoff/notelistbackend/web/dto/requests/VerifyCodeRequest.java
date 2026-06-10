package ru.reshetoff.notelistbackend.web.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на верификацию email")
public record VerifyCodeRequest(
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Verification code is required")
        String code
) {
}
