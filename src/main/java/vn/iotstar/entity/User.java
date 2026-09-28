package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity @Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_USERNAME = "username";
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String username;
    @Column(nullable = false, length = 150)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "full_name", columnDefinition = "nvarchar(200)")
    private String fullName;
    @Column(name = "image_url", length = 1000)
    private String imageUrl;
    @Builder.Default @Column(nullable = false)
    private boolean enabled = false;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
}
