package com.pmt.ikportal.web;

import jakarta.servlet.http.HttpSession;

public final class SessionKeys {

    public static final String USER_ID = "AUTH_USER_ID";
    public static final String USER_EMAIL = "AUTH_USER_EMAIL";
    public static final String USER_NAME = "AUTH_USER_NAME";

    private SessionKeys() {
    }

    public static Long userId(HttpSession session) {
        Object value = session.getAttribute(USER_ID);
        if (value == null) {
            throw new BusinessException("Oturum bulunamadı, lütfen tekrar giriş yapın.");
        }
        return (Long) value;
    }
}
