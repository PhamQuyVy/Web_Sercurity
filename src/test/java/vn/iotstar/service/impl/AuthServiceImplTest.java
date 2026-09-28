package vn.iotstar.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.entity.*;
import vn.iotstar.repository.*;
import vn.iotstar.service.OtpService;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock UserRepository users;
    @Mock RoleRepository roles;
    @Mock PasswordEncoder encoder;
    @Mock OtpService otp;
    @InjectMocks AuthServiceImpl service;
    private RegisterDTO dto;

    @BeforeEach void setUp() {
        dto = new RegisterDTO(); dto.setUsername("user01"); dto.setEmail("user01@example.com");
        dto.setFullName("User One"); dto.setPassword("secret12"); dto.setConfirmPassword("secret12");
    }

    @Test void registerStoresDisabledUserAndSendsOtp() {
        when(roles.findByName("ROLE_USER")).thenReturn(Optional.of(Role.builder().name("ROLE_USER").build()));
        when(encoder.encode("secret12")).thenReturn("hash");
        service.register(dto);
        ArgumentCaptor<User> user = ArgumentCaptor.forClass(User.class);
        verify(users).save(user.capture()); verify(otp).sendRegisterOtp("user01@example.com");
        assertThat(user.getValue().isEnabled()).isFalse();
        assertThat(user.getValue().getPassword()).isEqualTo("hash");
    }

    @Test void registerRejectsMismatchedPassword() {
        dto.setConfirmPassword("different");
        assertThatThrownBy(() -> service.register(dto)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("không khớp");
        verify(users, never()).save(any());
    }

    @Test void verifyRegisterEnablesUserAfterValidOtp() {
        User user = User.builder().email(dto.getEmail()).enabled(false).build();
        when(otp.verifyRegisterOtp(dto.getEmail(), "123456")).thenReturn(true);
        when(users.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        assertThat(service.verifyRegister(dto.getEmail(), "123456")).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }
}
