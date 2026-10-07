package com.pmt.ikportal.controller;

import com.pmt.ikportal.domain.AppUser;
import com.pmt.ikportal.service.AuthService;
import com.pmt.ikportal.web.BusinessException;
import com.pmt.ikportal.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute(SessionKeys.USER_ID) != null) {
            return "redirect:/";
        }
        model.addAttribute("pageTitle", "Giriş Yap");
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        try {
            AppUser user = authService.login(email, password);
            session.setAttribute(SessionKeys.USER_ID, user.getId());
            session.setAttribute(SessionKeys.USER_EMAIL, user.getEmail());
            session.setAttribute(SessionKeys.USER_NAME, user.getDisplayName());
            return "redirect:/";
        } catch (BusinessException e) {
            model.addAttribute("pageTitle", "Giriş Yap");
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("email", email);
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("pageTitle", "Kayıt Ol");
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String email,
                             @RequestParam(required = false) String displayName,
                             @RequestParam String password,
                             @RequestParam String passwordConfirm,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            authService.register(email, displayName, password, passwordConfirm);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Kaydınız oluşturuldu. Şimdi giriş yapabilirsiniz.");
            return "redirect:/login";
        } catch (BusinessException e) {
            model.addAttribute("pageTitle", "Kayıt Ol");
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("email", email);
            model.addAttribute("displayName", displayName);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
