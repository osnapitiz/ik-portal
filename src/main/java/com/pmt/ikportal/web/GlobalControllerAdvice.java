package com.pmt.ikportal.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Map;

@ControllerAdvice
public class GlobalControllerAdvice {

    /** Her sayfada ust bardaki kullanici bilgisi icin. */
    @ModelAttribute("currentUser")
    public Map<String, Object> currentUser(HttpSession session) {
        Object id = session.getAttribute(SessionKeys.USER_ID);
        if (id == null) {
            return null;
        }
        return Map.of(
                "id", id,
                "email", String.valueOf(session.getAttribute(SessionKeys.USER_EMAIL)),
                "name", String.valueOf(session.getAttribute(SessionKeys.USER_NAME)));
    }

    /** Is kurali hatalarini, gelinen sayfaya kirmizi uyari olarak geri yansitir. */
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException ex, HttpServletRequest request) {
        RequestContextUtils.getOutputFlashMap(request).put("errorMessage", ex.getMessage());

        return "redirect:" + safeReferer(request);
    }

    /** Acik yonlendirmeyi onlemek icin Referer bilgisinden yalnizca yol kismi kullanilir. */
    private String safeReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "/";
        }
        try {
            String path = java.net.URI.create(referer).getPath();
            return path == null || path.isBlank() ? "/" : path;
        } catch (IllegalArgumentException e) {
            return "/";
        }
    }
}
