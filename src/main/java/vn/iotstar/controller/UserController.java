package vn.iotstar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.service.UserService;

@Controller @RequestMapping("/users") @RequiredArgsConstructor
public class UserController {
    private final UserService service;
    @GetMapping String list(@RequestParam(name="keyword",defaultValue="") String keyword,@RequestParam(name="page",defaultValue="0") int page,@RequestParam(name="size",defaultValue="10") int size,Model m) {
        m.addAttribute("users",service.findAll(keyword,page,size)); m.addAttribute("keyword",keyword); m.addAttribute("size",size); return "users/list";
    }
    @GetMapping("/create") String createForm(Model m) { UserDTO dto=new UserDTO(); dto.setEnabled(true); dto.setRoleName("ROLE_USER"); form(m,dto,"create"); return "users/form"; }
    @PostMapping("/create") String create(@Valid @ModelAttribute UserDTO dto,BindingResult result,Model m,RedirectAttributes redirect) {
        if (result.hasErrors()) { form(m,dto,"create"); return "users/form"; }
        try { service.create(dto); redirect.addFlashAttribute("success","Thêm user thành công"); return "redirect:/users"; }
        catch (IllegalArgumentException e) { result.reject("user.error",e.getMessage()); form(m,dto,"create"); return "users/form"; }
    }
    @GetMapping("/edit/{id}") String editForm(@PathVariable("id") Long id,Model m) { form(m,service.findById(id),"edit"); return "users/form"; }
    @PostMapping("/edit/{id}") String edit(@PathVariable("id") Long id,@Valid @ModelAttribute UserDTO dto,BindingResult result,Model m,RedirectAttributes redirect) {
        dto.setId(id); if (result.hasErrors()) { form(m,dto,"edit"); return "users/form"; }
        try { service.update(id,dto); redirect.addFlashAttribute("success","Cập nhật user thành công"); return "redirect:/users"; }
        catch (IllegalArgumentException e) { result.reject("user.error",e.getMessage()); form(m,dto,"edit"); return "users/form"; }
    }
    @PostMapping("/delete/{id}") String delete(@PathVariable("id") Long id,RedirectAttributes redirect) {
        try { service.delete(id); redirect.addFlashAttribute("success","Xóa user thành công"); }
        catch (RuntimeException e) { redirect.addFlashAttribute("error",e.getMessage()); } return "redirect:/users";
    }
    private void form(Model m,UserDTO dto,String mode) { m.addAttribute("userDTO",dto); m.addAttribute("mode",mode); m.addAttribute("roles",new String[]{"ROLE_USER","ROLE_ADMIN"}); }
}
