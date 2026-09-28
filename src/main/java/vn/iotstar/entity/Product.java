package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "nvarchar(200)")
    private String name;
    @Column(columnDefinition = "nvarchar(max)")
    private String description;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;
    @Column(name = "image_url", length = 1000)
    private String imageUrl;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Builder.Default @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
