package ru.reshetoff.notelistbackend.web.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.reshetoff.notelistbackend.domain.entity.User;
import ru.reshetoff.notelistbackend.domain.entity.VerificationToken;
import ru.reshetoff.notelistbackend.domain.exception.InvalidVerificationCodeException;
import ru.reshetoff.notelistbackend.domain.exception.UserNotFoundException;
import ru.reshetoff.notelistbackend.domain.repository.UserRepository;
import ru.reshetoff.notelistbackend.domain.repository.VerificationTokenRepository;
import ru.reshetoff.notelistbackend.domain.service.VerificationService;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final TransactionTemplate transactionTemplate;

    @Value("${spring.mail.from}")
    private String fromAddress;
    @Value("${verification.test-token}")
    private String testToken;
    @Value("${verification.test-code}")
    private String testCode;

    @Override
    @Transactional
    public void sendVerificationCode(String email, String testToken) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(email, true)
        );

        verificationTokenRepository.deleteByUser(user);

        String code = (testToken != null && testToken.equals(this.testToken)) ? testCode :
                String.valueOf(100000 + ThreadLocalRandom.current().nextInt(900000));

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setCode(code);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(verificationToken);

        if (testToken == null || !testToken.equals(this.testToken)) {
            String subject = "Код подтверждения";
            String body = """
                    <div style="text-align: center; font-family: Arial, sans-serif;">
                        <h2>Добро пожаловать в Note List!</h2>
                        <p>Ваш код для подтверждения email:</p>
                        <div style="font-size: 32px; font-weight: bold; letter-spacing: 10px; 
                                    background: #f5f5f5; padding: 20px; margin: 20px auto; 
                                    width: 200px; border-radius: 8px;">
                            %s
                        </div>
                        <p>Код действителен 24 часа.</p>
                        <p>Если вы не запрашивали код, просто проигнорируйте это письмо.</p>
                    </div>
                    """.formatted(code);

            sendHtmlEmail(user.getEmail(), subject, body);
        }
    }

    @Override
    public void verifyCode(String email, String code) {
        VerificationToken verificationToken = verificationTokenRepository.findByUserEmail(email).orElseThrow(
                () -> new InvalidVerificationCodeException("Verification code for email: " + email + " not found")
        );

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            transactionTemplate.execute(status -> {
                verificationTokenRepository.delete(verificationToken);
                return null;
            });
            throw new InvalidVerificationCodeException("Verification code expired for email: " + email);
        }

        if (verificationToken.getAttempts() >= 5) {
            transactionTemplate.execute(status -> {
                verificationTokenRepository.delete(verificationToken);
                return null;
            });
            throw new InvalidVerificationCodeException("Too many attempts for email: " + email);
        }

        if (!verificationToken.getCode().equals(code)) {
            transactionTemplate.execute(status -> {
                verificationTokenRepository.incrementAttempts(verificationToken);
                return null;
            });
            throw new InvalidVerificationCodeException("Invalid verification code for email: " + email);
        }

        transactionTemplate.execute(status -> {
            User user = verificationToken.getUser();
            user.setVerified(true);
            userRepository.save(user);

            verificationTokenRepository.delete(verificationToken);
            return null;
        });
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
