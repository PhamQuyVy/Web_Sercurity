package vn.iotstar.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final String fullName;
    private final String imageUrl;
    private final String role;
    private final boolean enabled;
    public CustomUserDetails(Long id, String username, String email, String password, String fullName, String imageUrl, String role, boolean enabled) {
        this.id=id; this.username=username; this.email=email; this.password=password; this.fullName=fullName; this.imageUrl=imageUrl; this.role=role; this.enabled=enabled;
    }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(new SimpleGrantedAuthority(role)); }
}
