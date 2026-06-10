package ru.reshetoff.notelistbackend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.reshetoff.notelistbackend.domain.entity.User;
import ru.reshetoff.notelistbackend.domain.exception.AccountNotVerifiedException;
import ru.reshetoff.notelistbackend.domain.exception.InvalidCredentialsException;
import ru.reshetoff.notelistbackend.domain.exception.UserNotFoundException;
import ru.reshetoff.notelistbackend.domain.service.UserService;
import ru.reshetoff.notelistbackend.domain.service.VerificationService;
import ru.reshetoff.notelistbackend.web.dto.requests.LoginRequest;
import ru.reshetoff.notelistbackend.web.dto.requests.RefreshTokenRequest;
import ru.reshetoff.notelistbackend.web.dto.requests.RegisterUserRequest;
import ru.reshetoff.notelistbackend.web.dto.response.AuthResponse;
import ru.reshetoff.notelistbackend.web.dto.response.ErrorResponse;
import ru.reshetoff.notelistbackend.web.dto.response.UserResponse;
import ru.reshetoff.notelistbackend.web.mapper.AuthMapper;
import ru.reshetoff.notelistbackend.web.security.CustomUserDetails;
import ru.reshetoff.notelistbackend.web.security.JwtService;
import ru.reshetoff.notelistbackend.web.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Auth", description = "Аутентификация и регистрация")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final VerificationService verificationService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Пароль должен содержать минимум 8 символов, заглавную и строчную букву, цифру и спецсимвол (@#$%^&+=!)"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные для регистрации",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                                "displayName": "Иван Иванов",
                                "phoneNumber": "+79999999999",
                                "email": "ivan@example.com",
                                "password": "password123"
                            }
                            """)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно зарегистрирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные данные (email или password отсутствуют или невалидны)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ValidationError",
                                    value = """
                                            {
                                                "code": "VALIDATION_FAILED",
                                                "level": "error",
                                                "message": "Some fields are filled incorrectly",
                                                "details": [
                                                    {
                                                        "field": "email",
                                                        "message": "Email is required"
                                                    },
                                                    {
                                                        "field": "password",
                                                        "message": "Password must be at least 8 characters"
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь с таким email уже существует",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ConflictError",
                                    value = """
                                            {
                                                "code": "EMAIL_ALREADY_EXISTS",
                                                "level": "error",
                                                "message": "Email already exists: ivan@example.com",
                                                "details": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        User user = userService.registerUser(AuthMapper.toEntity(request));
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(customUserDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AuthResponse(accessToken, user.getRefreshToken())
        );
    }

    @PostMapping("/login")
    @Operation(
            summary = "Вход в систему",
            description = "Пароль должен содержать минимум 8 символов, заглавную и строчную букву, цифру и спецсимвол (@#$%^&+=!)"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Учётные данные пользователя",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                                "email": "ivan@example.com",
                                "password": "password123"
                            }
                            """)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация, возвращён JWT-токен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверный email или пароль",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "UnauthorizedError",
                                    value = """
                                            {
                                                "code": "UNAUTHORIZED",
                                                "level": "error",
                                                "message": "Invalid password",
                                                "details": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Аккаунт не верифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ForbiddenError",
                                    value = """
                                            {
                                                "code": "ACCOUNT_NOT_VERIFIED",
                                                "level": "error",
                                                "message": "Account not verified: ivan@example.com",
                                                "details": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user;
        try {
            user = userService.findByEmail(request.getEmail());
        } catch (UserNotFoundException e) {
            throw new InvalidCredentialsException();
        }

        if (!user.isVerified()) {
            throw new AccountNotVerifiedException(request.getEmail());
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }

        userService.updateRefreshToken(user);
        user = userService.findByEmail(request.getEmail());
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateAccessToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token, user.getRefreshToken()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление токена доступа")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Refresh токен для обновления",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                            }
                            """)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Токены успешно обновлены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh токен истёк или невалиден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "UnauthorizedError",
                                    value = """
                                            {
                                                "code": "UNAUTHORIZED",
                                                "level": "error",
                                                "message": "Refresh token expired",
                                                "details": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Аккаунт не верифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ForbiddenError",
                                    value = """
                                            {
                                                "code": "ACCOUNT_NOT_VERIFIED",
                                                "level": "error",
                                                "message": "Account not verified: ivan@example.com",
                                                "details": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Refresh токен не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "NotFoundError",
                                    value = """
                                            {
                                                "code": "USER_NOT_FOUND",
                                                "level": "error",
                                                "message": "User with identifier ... not found",
                                                "details": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        User user = userService.findByRefreshToken(request.getRefreshToken());

        if (!user.isVerified()) {
            throw new AccountNotVerifiedException(user.getEmail());
        }

        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        userService.updateRefreshToken(user);
        user = userService.findByEmail(user.getEmail());
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(customUserDetails);

        return ResponseEntity.ok(new AuthResponse(accessToken, user.getRefreshToken()));
    }

    @PostMapping("/send-verification")
    @Operation(
            summary = "Отправить письмо для верификации email",
            description = "На указанный email будет отправлено письмо со ссылкой для подтверждения. Ссылка действительна 24 часа."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Письмо отправлено",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "На ваш email отправлено письмо со ссылкой для подтверждения. Ссылка действительна 24 часа."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с таким email не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "code": "USER_NOT_FOUND",
                                        "level": "error",
                                        "message": "User with email ivan@example.com not found",
                                        "details": null
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<Map<String, String>> sendVerification(@RequestParam String email) {
        verificationService.sendVerificationEmail(email);
        return ResponseEntity.ok(Map.of(
                "message", "На ваш email отправлено письмо со ссылкой для подтверждения. Ссылка действительна 24 часа."
        ));
    }

    @GetMapping("/verify")
    @Operation(
            summary = "Подтвердить email по токену",
            description = "Подтверждает email пользователя. После успешной верификации пользователь может войти в систему."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email успешно подтверждён",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Email успешно подтверждён. Теперь вы можете войти в систему."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидный или просроченный токен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "code": "INVALID_VERIFICATION_TOKEN",
                                        "level": "error",
                                        "message": "Invalid or expired verification token: some-uuid",
                                        "details": null
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        verificationService.verifyEmail(token);
        return ResponseEntity.ok(Map.of(
                "message", "Email успешно подтверждён. Теперь вы можете войти в систему."
        ));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Получить данные текущего пользователя",
            description = "Возвращает профиль авторизованного пользователя: ID, имя, email, телефон, статус верификации и дату создания."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные пользователя успешно получены",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "id": "e7272769-a748-4279-a1cb-85a3f72bd289",
                                        "displayName": "Иван Иванов",
                                        "email": "ivan@example.com",
                                        "phoneNumber": "+79991234567",
                                        "isVerified": true,
                                        "createdAt": "2026-05-25T22:00:00"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован (токен истек, невалиден или отсутствует)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "code": "UNAUTHORIZED",
                                        "level": "error",
                                        "message": "Full authentication is required to access this resource",
                                        "details": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден в базе данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "code": "USER_NOT_FOUND",
                                        "level": "error",
                                        "message": "User not found",
                                        "details": null
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<UserResponse> getCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userService.findById(userId);
        return ResponseEntity.ok(AuthMapper.toResponse(user));
    }
}
