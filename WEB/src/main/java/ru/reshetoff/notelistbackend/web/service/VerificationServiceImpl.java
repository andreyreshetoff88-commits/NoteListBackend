package ru.reshetoff.notelistbackend.web.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.reshetoff.notelistbackend.domain.entity.User;
import ru.reshetoff.notelistbackend.domain.entity.VerificationToken;
import ru.reshetoff.notelistbackend.domain.exception.InvalidVerificationTokenException;
import ru.reshetoff.notelistbackend.domain.exception.UserNotFoundException;
import ru.reshetoff.notelistbackend.domain.repository.UserRepository;
import ru.reshetoff.notelistbackend.domain.repository.VerificationTokenRepository;
import ru.reshetoff.notelistbackend.domain.service.VerificationService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.from}")
    private String fromAddress;

    @Override
    public void sendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(email, true)
        );

        verificationTokenRepository.deleteByUser(user);

        String tokenValue = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(tokenValue);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/auth/verify?token=" + tokenValue;
        String subject = "Подтверждение email";
        String body = "<h2>Добро пожаловать в Note List!</h2>"
                + "<p>Для подтверждения email перейдите по ссылке:</p>"
                + "<a href=\"" + verificationLink + "\">" + verificationLink + "</a>"
                + "<p>Ссылка действительна 24 часа.</p>";

        sendHtmlEmail(user.getEmail(), subject, body);
    }

    @Override
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElseThrow(
                () -> new InvalidVerificationTokenException(token)
        );

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new InvalidVerificationTokenException(token);
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    private void sendHtmlEmail(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
