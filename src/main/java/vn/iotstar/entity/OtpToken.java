package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "otp_tokens", indexes = @Index(name = "idx_otp_email_type", columnList = "email,type"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String email;
    @Column(name = "otp_hash", nullable = false)
    private String otpHash;
    @Column(nullable = false, length = 30)
    private String type;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Builder.Default @Column(nullable = false)
    private int attempts = 0;
    @Builder.Default @Column(nullable = false)
    private boolean used = false;
    @Builder.Default @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
