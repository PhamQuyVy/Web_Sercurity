package vn.iotstar.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    @NotBlank(message = "Username không được để trống") @Size(max = 50)
    private String username;
    @NotBlank(message = "Email không được để trống") @Email(message = "Email không hợp lệ")
    private String email;
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    private String imageUrl;
    private String password;
    @NotBlank(message = "Vai trò không được để trống")
    private String roleName;
    private boolean enabled;
    private long productCount;
}
