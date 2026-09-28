package vn.iotstar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.service.*;
import java.util.List;

@Controller @RequiredArgsConstructor
public class HomeController {
    private final UserService users; private final ProductService products;
    @GetMapping("/") public String home(Model model) {
        model.addAttribute("userCount", users.count());
        model.addAttribute("productCount", products.count());
        List<ProductDTO> latest = products.findAll("", 0, 12).getContent();
        int split = Math.min(3, latest.size());
        model.addAttribute("promoProducts", latest.subList(0, split));
        model.addAttribute("latestProducts", latest.size() > split ? latest.subList(split, latest.size()) : latest);
        return "home";
    }
}
