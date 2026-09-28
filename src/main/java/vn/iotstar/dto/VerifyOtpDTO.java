package vn.iotstar.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VerifyOtpDTO {
    @NotBlank @Email private String email;
    @NotBlank @Pattern(regexp = "\\d{6}", message = "OTP phải gồm 6 chữ số") private String otp;
}
