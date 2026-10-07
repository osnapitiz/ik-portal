package com.pmt.ikportal.controller;

import com.pmt.ikportal.domain.EmployeeProfile;
import com.pmt.ikportal.service.ProfileService;
import com.pmt.ikportal.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final List<String> MARITAL_STATUSES = List.of("Bekar", "Evli", "Boşanmış", "Dul");
    private static final List<String> EMPLOYMENT_TYPES =
            List.of("Tam Zamanlı", "Yarı Zamanlı", "Sözleşmeli", "Stajyer", "Uzaktan");
    private static final List<String> BLOOD_TYPES =
            List.of("A Rh+", "A Rh-", "B Rh+", "B Rh-", "AB Rh+", "AB Rh-", "0 Rh+", "0 Rh-");
    private static final List<String> CURRENCIES = List.of("TRY", "USD", "EUR", "GBP");

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public String view(HttpSession session, Model model) {
        Long userId = SessionKeys.userId(session);
        boolean exists = profileService.find(userId).isPresent();

        model.addAttribute("profile", profileService.findOrEmpty(userId));
        model.addAttribute("editing", !exists);
        prepareForm(model);
        return "profile";
    }

    @GetMapping("/edit")
    public String edit(HttpSession session, Model model) {
        Long userId = SessionKeys.userId(session);

        model.addAttribute("profile", profileService.findOrEmpty(userId));
        model.addAttribute("editing", true);
        prepareForm(model);
        return "profile";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("profile") EmployeeProfile profile,
                       BindingResult binding,
                       HttpSession session,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Long userId = SessionKeys.userId(session);

        if (binding.hasErrors()) {
            model.addAttribute("editing", true);
            model.addAttribute("errorMessage", "Formda eksik veya hatalı alanlar var, lütfen kontrol edin.");
            prepareForm(model);
            return "profile";
        }

        profileService.save(userId, profile);
        redirectAttributes.addFlashAttribute("successMessage", "Bilgileriniz kaydedildi.");
        return "redirect:/profile";
    }

    private void prepareForm(Model model) {
        model.addAttribute("pageTitle", "Bilgilerim");
        model.addAttribute("activeMenu", "profile");
        model.addAttribute("maritalStatuses", MARITAL_STATUSES);
        model.addAttribute("employmentTypes", EMPLOYMENT_TYPES);
        model.addAttribute("bloodTypes", BLOOD_TYPES);
        model.addAttribute("currencies", CURRENCIES);
    }
}
