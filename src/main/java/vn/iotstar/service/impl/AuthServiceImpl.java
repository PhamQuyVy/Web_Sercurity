package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.entity.*;
import vn.iotstar.repository.*;
import vn.iotstar.service.*;

@Service @RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository users; private final RoleRepository roles; private final PasswordEncoder encoder; private final OtpService otpService;
    @Override @Transactional public void register(RegisterDTO dto) {
        if (users.existsByUsername(dto.getUsername())) throw new IllegalArgumentException("Username đã tồn tại");
        if (users.existsByEmail(dto.getEmail())) throw new IllegalArgumentException("Email đã tồn tại");
        if (!dto.getPassword().equals(dto.getConfirmPassword())) throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        Role role = roles.findByName("ROLE_USER").orElseThrow();
        users.save(User.builder().username(dto.getUsername()).email(dto.getEmail()).fullName(dto.getFullName())
                .password(encoder.encode(dto.getPassword())).enabled(false).role(role).build());
        otpService.sendRegisterOtp(dto.getEmail());
    }
    @Override @Transactional public boolean verifyRegister(String email, String otp) {
        if (!otpService.verifyRegisterOtp(email, otp)) return false;
        User user = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
        user.setEnabled(true); return true;
    }
    @Override public void resendRegisterOtp(String email) {
        User u = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
        if (u.isEnabled()) throw new IllegalArgumentException("Tài khoản đã kích hoạt");
        otpService.sendRegisterOtp(email);
    }
    @Override public void requestPasswordReset(String email) {
        if (users.findByEmail(email).isEmpty()) throw new IllegalArgumentException("Email không tồn tại");
        otpService.sendResetPasswordOtp(email);
    }
    @Override @Transactional public void resetPassword(String email, String otp, String newPassword) {
        if (!otpService.verifyResetPasswordOtp(email, otp)) throw new IllegalArgumentException("OTP không hợp lệ hoặc đã hết hạn");
        User user = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
        user.setPassword(encoder.encode(newPassword));
    }
}
