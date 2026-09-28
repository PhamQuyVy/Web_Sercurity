package vn.iotstar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.service.*;

@Controller @RequestMapping("/products") @RequiredArgsConstructor
public class ProductController {
    private final ProductService products; private final UserService users;
    @GetMapping String list(@RequestParam(name="keyword",defaultValue="") String keyword,@RequestParam(name="page",defaultValue="0") int page,@RequestParam(name="size",defaultValue="10") int size,Model m) {
        m.addAttribute("products",products.findAll(keyword,page,size)); m.addAttribute("keyword",keyword); m.addAttribute("size",size); return "products/list";
    }
    @GetMapping("/create") String createForm(Model m) { form(m,new ProductDTO(),"create"); return "products/form"; }
    @PostMapping("/create") String create(@Valid @ModelAttribute ProductDTO dto,BindingResult result,@RequestParam(name="image",required=false) MultipartFile image,Model m,RedirectAttributes redirect) {
        if (result.hasErrors()) { form(m,dto,"create"); return "products/form"; }
        try { products.create(dto,image); redirect.addFlashAttribute("success","Thêm sản phẩm thành công"); return "redirect:/products"; }
        catch (RuntimeException e) { result.reject("product.error",e.getMessage()); form(m,dto,"create"); return "products/form"; }
    }
    @GetMapping("/edit/{id}") String editForm(@PathVariable("id") Long id,Model m) { form(m,products.findById(id),"edit"); return "products/form"; }
    @PostMapping("/edit/{id}") String edit(@PathVariable("id") Long id,@Valid @ModelAttribute ProductDTO dto,BindingResult result,@RequestParam(name="image",required=false) MultipartFile image,Model m,RedirectAttributes redirect) {
        dto.setId(id); if (result.hasErrors()) { form(m,dto,"edit"); return "products/form"; }
        try { products.update(id,dto,image); redirect.addFlashAttribute("success","Cập nhật sản phẩm thành công"); return "redirect:/products"; }
        catch (RuntimeException e) { result.reject("product.error",e.getMessage()); form(m,dto,"edit"); return "products/form"; }
    }
    @PostMapping("/delete/{id}") String delete(@PathVariable("id") Long id,RedirectAttributes redirect) {
        try { products.delete(id); redirect.addFlashAttribute("success","Xóa sản phẩm thành công"); }
        catch (RuntimeException e) { redirect.addFlashAttribute("error",e.getMessage()); } return "redirect:/products";
    }
    private void form(Model m,ProductDTO dto,String mode) { m.addAttribute("productDTO",dto); m.addAttribute("mode",mode); m.addAttribute("userChoices",users.choices()); }
}
