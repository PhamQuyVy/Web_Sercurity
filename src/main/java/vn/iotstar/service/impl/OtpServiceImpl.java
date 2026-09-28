package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.entity.OtpToken;
import vn.iotstar.repository.OtpTokenRepository;
import vn.iotstar.service.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private static final String REGISTER = "REGISTER", RESET = "RESET_PASSWORD";
    private final OtpTokenRepository repository; private final EmailService emailService; private final PasswordEncoder encoder;
    private final SecureRandom random = new SecureRandom();
    @Value("${app.otp.expiration-minutes:5}") private long expirationMinutes;
    @Value("${app.otp.max-attempts:5}") private int maxAttempts;
    @Override @Transactional public void sendRegisterOtp(String email) { send(email, REGISTER, "OTP xác nhận đăng ký"); }
    @Override @Transactional public boolean verifyRegisterOtp(String email, String otp) { return verify(email, otp, REGISTER); }
    @Override @Transactional public void sendResetPasswordOtp(String email) { send(email, RESET, "OTP đặt lại mật khẩu"); }
    @Override @Transactional public boolean verifyResetPasswordOtp(String email, String otp) { return verify(email, otp, RESET); }
    private void send(String email, String type, String subject) {
        repository.deleteByEmailAndType(email, type);
        String otp = "%06d".formatted(random.nextInt(1_000_000));
        repository.save(OtpToken.builder().email(email).type(type).otpHash(encoder.encode(otp))
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes)).build());
        emailService.sendOtp(email, otp, subject);
    }
    private boolean verify(String email, String otp, String type) {
        OtpToken token = repository.findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(email, type).orElse(null);
        if (token == null || token.getExpiresAt().isBefore(LocalDateTime.now()) || token.getAttempts() >= maxAttempts) return false;
        token.setAttempts(token.getAttempts() + 1);
        boolean valid = encoder.matches(otp, token.getOtpHash());
        if (valid) token.setUsed(true);
        repository.save(token); return valid;
    }
}
