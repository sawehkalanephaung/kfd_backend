package com.kfd.api.kfd_backend.auth;

import com.kfd.api.kfd_backend.global.mail.MailService;
import com.kfd.api.kfd_backend.user.User;
import com.kfd.api.kfd_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.admin-url:http://localhost:3000}")
    private String frontendAdminUrl;

    @Override
    @Transactional
    public void processForgotPassword(ForgotPasswordRequestDTO request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.email());
        if (optionalUser.isEmpty()) {
            // To prevent email enumeration, we just return silently
            log.warn("Forgot password requested for non-existent email: {}", request.email());
            return;
        }

        User user = optionalUser.get();
        String tokenString = UUID.randomUUID().toString();

        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenString)
                .user(user)
                .expiryDate(OffsetDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(token);

        String resetLink = frontendAdminUrl + "/reset-password?token=" + tokenString;
        mailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        log.info("Password reset email sent to {}", user.getEmail());
    }

    @Override
    @Transactional
    public void processResetPassword(ResetPasswordRequestDTO request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.token())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // Invalidate the token after successful use
        tokenRepository.delete(resetToken);
        log.info("Password successfully reset for user {}", user.getEmail());
    }
}
