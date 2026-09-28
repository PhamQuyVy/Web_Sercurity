package vn.iotstar.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User u = userRepository.findByUsernameOrEmail(login, login)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + login));
        return new CustomUserDetails(u.getId(), u.getUsername(), u.getEmail(), u.getPassword(), u.getFullName(), u.getImageUrl(), u.getRole().getName(), u.isEnabled());
    }
}
