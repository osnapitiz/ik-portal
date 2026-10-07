package com.pmt.ikportal.controller;

import com.pmt.ikportal.domain.LeaveRequest;
import com.pmt.ikportal.domain.LeaveStatus;
import com.pmt.ikportal.domain.LeaveType;
import com.pmt.ikportal.service.LeaveService;
import com.pmt.ikportal.web.BusinessException;
import com.pmt.ikportal.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        Long userId = SessionKeys.userId(session);

        if (!model.containsAttribute("leaveForm")) {
            model.addAttribute("leaveForm", new LeaveRequest());
        }
        prepare(model, userId);
        return "leaves";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("leaveForm") LeaveRequest leaveForm,
                         BindingResult binding,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Long userId = SessionKeys.userId(session);

        if (binding.hasErrors()) {
            model.addAttribute("errorMessage", "Formda eksik veya hatalı alanlar var, lütfen kontrol edin.");
            prepare(model, userId);
            return "leaves";
        }

        try {
            leaveService.create(userId, leaveForm);
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            prepare(model, userId);
            return "leaves";
        }

        redirectAttributes.addFlashAttribute("successMessage", "İzin talebiniz oluşturuldu.");
        return "redirect:/leaves";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        leaveService.cancel(SessionKeys.userId(session), id);
        redirectAttributes.addFlashAttribute("successMessage", "İzin talebi iptal edildi.");
        return "redirect:/leaves";
    }

    @PostMapping("/{id}/decide")
    public String decide(@PathVariable Long id,
                         @RequestParam LeaveStatus decision,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        leaveService.decide(SessionKeys.userId(session), id, decision);
        redirectAttributes.addFlashAttribute("successMessage",
                decision == LeaveStatus.ONAYLANDI ? "İzin talebi onaylandı." : "İzin talebi reddedildi.");
        return "redirect:/leaves";
    }

    private void prepare(Model model, Long userId) {
        model.addAttribute("pageTitle", "İzinlerim");
        model.addAttribute("activeMenu", "leaves");
        model.addAttribute("leaveTypes", LeaveType.values());
        model.addAttribute("leaves", leaveService.list(userId));
        model.addAttribute("balance", leaveService.balance(userId));
    }
}
