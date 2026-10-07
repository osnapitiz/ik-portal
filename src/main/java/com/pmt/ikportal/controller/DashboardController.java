package com.pmt.ikportal.controller;

import com.pmt.ikportal.domain.EmployeeProfile;
import com.pmt.ikportal.domain.LeaveRequest;
import com.pmt.ikportal.service.LeaveService;
import com.pmt.ikportal.service.ProfileService;
import com.pmt.ikportal.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    private final ProfileService profileService;
    private final LeaveService leaveService;

    public DashboardController(ProfileService profileService, LeaveService leaveService) {
        this.profileService = profileService;
        this.leaveService = leaveService;
    }

    @GetMapping("/")
    public String dashboard(HttpSession session, Model model) {
        Long userId = SessionKeys.userId(session);

        Optional<EmployeeProfile> profile = profileService.find(userId);
        List<LeaveRequest> recentLeaves = leaveService.list(userId).stream().limit(5).toList();

        model.addAttribute("pageTitle", "Özet");
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("profile", profile.orElse(null));
        model.addAttribute("profileComplete", profile.isPresent());
        model.addAttribute("balance", leaveService.balance(userId));
        model.addAttribute("recentLeaves", recentLeaves);
        return "dashboard";
    }
}
