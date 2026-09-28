package vn.iotstar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.*;
import vn.iotstar.service.AuthService;

@Controller @RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;
    @GetMapping("/login") String login() { return "auth/login"; }
    @GetMapping("/register") String registerForm(Model m) { m.addAttribute("registerDTO",new RegisterDTO()); return "auth/register"; }
    @PostMapping("/register") String register(@Valid @ModelAttribute RegisterDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) return "auth/register";
        try { auth.register(dto); VerifyOtpDTO verify=new VerifyOtpDTO(); verify.setEmail(dto.getEmail()); model.addAttribute("verifyOtpDTO",verify); return "auth/verify-otp"; }
        catch (IllegalArgumentException e) { result.reject("register.error",e.getMessage()); return "auth/register"; }
        catch (MailException e) { result.reject("register.mail","Không thể gửi OTP. Hãy kiểm tra cấu hình MAIL_USERNAME và MAIL_PASSWORD."); return "auth/register"; }
    }
    @PostMapping("/verify-otp") String verify(@Valid @ModelAttribute VerifyOtpDTO dto, BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) return "auth/verify-otp";
        if (!auth.verifyRegister(dto.getEmail(),dto.getOtp())) { result.reject("otp.error","OTP không hợp lệ hoặc đã hết hạn"); return "auth/verify-otp"; }
        redirect.addFlashAttribute("success","Kích hoạt tài khoản thành công"); return "redirect:/login";
    }
    @PostMapping("/resend-register-otp") String resend(@RequestParam("email") String email, Model model) {
        VerifyOtpDTO dto=new VerifyOtpDTO(); dto.setEmail(email); model.addAttribute("verifyOtpDTO",dto);
        try { auth.resendRegisterOtp(email); model.addAttribute("success","Đã gửi lại OTP"); }
        catch (IllegalArgumentException e) { model.addAttribute("error",e.getMessage()); }
        catch (MailException e) { model.addAttribute("error","Không thể gửi OTP. Hãy kiểm tra cấu hình email."); }
        return "auth/verify-otp";
    }
    @GetMapping("/forgot-password") String forgotForm(Model m) { m.addAttribute("forgotPasswordDTO",new ForgotPasswordDTO()); return "auth/forgot-password"; }
    @PostMapping("/forgot-password") String forgot(@Valid @ModelAttribute ForgotPasswordDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) return "auth/forgot-password";
        try { auth.requestPasswordReset(dto.getEmail()); ResetPasswordDTO reset=new ResetPasswordDTO(); reset.setEmail(dto.getEmail()); model.addAttribute("resetPasswordDTO",reset); return "auth/reset-password"; }
        catch (IllegalArgumentException e) { result.reject("forgot.error",e.getMessage()); return "auth/forgot-password"; }
        catch (MailException e) { result.reject("forgot.mail","Không thể gửi OTP. Hãy kiểm tra cấu hình MAIL_USERNAME và MAIL_PASSWORD."); return "auth/forgot-password"; }
    }
    @PostMapping("/reset-password") String reset(@Valid @ModelAttribute ResetPasswordDTO dto, BindingResult result, RedirectAttributes redirect) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) result.reject("password.error","Mật khẩu xác nhận không khớp");
        if (result.hasErrors()) return "auth/reset-password";
        try { auth.resetPassword(dto.getEmail(),dto.getOtp(),dto.getPassword()); redirect.addFlashAttribute("success","Đổi mật khẩu thành công"); return "redirect:/login"; }
        catch (IllegalArgumentException e) { result.reject("reset.error",e.getMessage()); return "auth/reset-password"; }
    }
}
