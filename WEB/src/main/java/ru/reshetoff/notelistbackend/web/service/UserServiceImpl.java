package ru.reshetoff.notelistbackend.web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.reshetoff.notelistbackend.domain.entity.User;
import ru.reshetoff.notelistbackend.domain.exception.EmailAlreadyExistsException;
import ru.reshetoff.notelistbackend.domain.exception.UserNotFoundException;
import ru.reshetoff.notelistbackend.domain.repository.UserRepository;
import ru.reshetoff.notelistbackend.domain.service.UserService;
import ru.reshetoff.notelistbackend.web.security.CustomUserDetails;
import ru.reshetoff.notelistbackend.web.security.JwtService;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public User registerUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        Date expiration = jwtService.extractExpiration(refreshToken);
        savedUser.setRefreshToken(refreshToken);
        savedUser.setRefreshTokenExpiry(expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        return savedUser;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(email, true)
        );
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                () -> new UserNotFoundException(phoneNumber, false)
        );
    }

    @Override
    public User findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new UserNotFoundException(refreshToken, false)
        );
    }

    @Override
    public List<User> findAllByPhoneNumbers(List<String> phoneNumbers) {
        return phoneNumbers.stream()
                .map(userRepository::findByPhoneNumber)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void updateRefreshToken(User user) {
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String refreshToken = jwtService.generateRefreshToken(customUserDetails);
        Date expiration = jwtService.extractExpiration(refreshToken);

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        userRepository.save(user);
    }
}
