package vn.iotstar.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.entity.*;
import vn.iotstar.repository.*;

@Component @RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roles; private final UserRepository users; private final PasswordEncoder encoder;
    @Override @Transactional public void run(String... args) {
        roles.findByName("ROLE_USER").orElseGet(() -> roles.save(Role.builder().name("ROLE_USER").build()));
        Role adminRole = roles.findByName("ROLE_ADMIN").orElseGet(() -> roles.save(Role.builder().name("ROLE_ADMIN").build()));
        if (!users.existsByUsername("admin")) users.save(User.builder().username("admin").email("admin@example.com")
                .fullName("Quản trị viên").imageUrl("https://ui-avatars.com/api/?name=Admin&background=2563eb&color=fff")
                .password(encoder.encode("Admin@123")).enabled(true).role(adminRole).build());
    }
}
