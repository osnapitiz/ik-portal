package com.pmt.ikportal.service;

import com.pmt.ikportal.domain.AppUser;
import com.pmt.ikportal.repository.AppUserRepository;
import com.pmt.ikportal.web.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository users;
    private final PasswordHasher hasher;

    public AuthService(AppUserRepository users, PasswordHasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    @Transactional
    public AppUser register(String email, String displayName, String rawPassword, String passwordConfirm) {
        String normalized = normalize(email);

        if (!rawPassword.equals(passwordConfirm)) {
            throw new BusinessException("Şifreler birbiriyle eşleşmiyor.");
        }
        if (rawPassword.length() < 6) {
            throw new BusinessException("Şifre en az 6 karakter olmalıdır.");
        }
        if (users.existsByEmailIgnoreCase(normalized)) {
            throw new BusinessException("Bu e-posta adresi ile daha önce kayıt oluşturulmuş.");
        }

        String salt = hasher.newSalt();

        AppUser user = new AppUser();
        user.setEmail(normalized);
        user.setDisplayName(displayName == null || displayName.isBlank() ? normalized : displayName.trim());
        user.setPasswordSalt(salt);
        user.setPasswordHash(hasher.hash(rawPassword, salt));

        return users.save(user);
    }

    @Transactional(readOnly = true)
    public AppUser login(String email, String rawPassword) {
        AppUser user = users.findByEmailIgnoreCase(normalize(email))
                .orElseThrow(() -> new BusinessException("E-posta veya şifre hatalı."));

        if (!hasher.matches(rawPassword, user.getPasswordSalt(), user.getPasswordHash())) {
            throw new BusinessException("E-posta veya şifre hatalı.");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public AppUser requireUser(Long userId) {
        return users.findById(userId)
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı."));
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
