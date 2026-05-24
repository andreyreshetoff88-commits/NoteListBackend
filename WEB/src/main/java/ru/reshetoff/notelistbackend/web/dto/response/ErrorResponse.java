package ru.reshetoff.notelistbackend.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сообщение о произошедшей ошибке")
public class ErrorResponse {
    @Schema(description = "Машинно-читаемый код ошибки", example = "VALIDATION_FAILED")
    private String code;

    @Schema(description = "Уровень серьезности", example = "error", allowableValues = {"info", "warning", "error"})
    private String level;

    @Schema(description = "Человеко-читаемое сообщение", example = "Некоторые поля заполнены неверно")
    private String message;

    @Schema(description = "Детали ошибки (опционально)")
    private List<ErrorDetail> details;

    public ErrorResponse(String code, String level, String message) {
        this.code = code;
        this.level = level;
        this.message = message;
        this.details = null;
    }
}
