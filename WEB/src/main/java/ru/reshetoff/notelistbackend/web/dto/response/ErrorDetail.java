package ru.reshetoff.notelistbackend.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сведения о произошедшей ошибке")
public class ErrorDetail {
    @Schema(description = "Поле, которое содержит ошибку", example = "email")
    private String field;

    @Schema(description = "Описание ошибки", example = "Должен быть валидным email адресом")
    private String message;
}