package com.photoalbum.controller;

import com.photoalbum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConfirmController {

    @Autowired
    private UserService userService;

    @GetMapping("/confirm")
    public String confirmRegistration(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        System.out.println("=== ПОДТВЕРЖДЕНИЕ РЕГИСТРАЦИИ ===");
        System.out.println("Токен получен: " + token);
        
        boolean confirmed = userService.confirmRegistration(token);
        System.out.println("Результат подтверждения: " + confirmed);
        
        if (confirmed) {
            redirectAttributes.addFlashAttribute("message", "Ваш email успешно подтверждён! Теперь вы можете войти.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Ссылка недействительна или истекла.");
            return "redirect:/register";
        }
    }
}