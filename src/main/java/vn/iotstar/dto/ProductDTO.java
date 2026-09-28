package vn.iotstar.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    private String description;
    @NotNull(message = "Giá không được để trống") @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;
    private String imageUrl;
    @NotNull(message = "Hãy chọn chủ sở hữu")
    private Long userId;
    private String username;
}
