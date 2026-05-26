package ru.reshetoff.notelistbackend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.reshetoff.notelistbackend.domain.entity.Country;
import ru.reshetoff.notelistbackend.domain.service.CountryService;

import java.util.List;

@Tag(name = "Utils", description = "Вспомогательные справочные данные")
@RestController
@RequestMapping("/utils/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @GetMapping
    @Operation(
            summary = "Получить список доступных стран",
            description = "Возвращает список стран с кодами, телефонными масками и ссылками на флаги для регистрации"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список стран",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    [
                        {
                            "id": "62a2627f-53fd-4323-b273-b3cf6954129c",
                            "countryCode": "RU",
                            "phoneCode": "7",
                            "phoneMask": "+7 (XXX) XXX-XX-XX",
                            "flagUrl": "http://localhost:9000/flags/ru.png"
                        },
                        {
                            "id": "6ffee131-b578-4854-9065-71b86582e668",
                            "countryCode": "KG",
                            "phoneCode": "996",
                            "phoneMask": "+996 (XXX) XXX-XXX",
                            "flagUrl": "http://localhost:9000/flags/kg.png"
                        }
                    ]
                    """)
                    )
            )
    })
    public ResponseEntity<List<Country>> getCountries() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }
}
