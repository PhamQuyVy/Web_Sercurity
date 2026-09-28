package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.iotstar.service.EmailService;

@Service @RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender sender;
    @Value("${app.mail.from}") private String from;
    @Override public void sendOtp(String email, String otp, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (from != null && !from.isBlank()) message.setFrom(from);
        message.setTo(email); message.setSubject(subject);
        message.setText("Mã OTP của bạn là: " + otp + "\nMã có hiệu lực trong 5 phút. Không chia sẻ mã này.");
        sender.send(message);
    }
}
