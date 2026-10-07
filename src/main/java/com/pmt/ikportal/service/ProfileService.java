package com.pmt.ikportal.service;

import com.pmt.ikportal.domain.EmployeeProfile;
import com.pmt.ikportal.repository.EmployeeProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProfileService {

    private final EmployeeProfileRepository profiles;

    public ProfileService(EmployeeProfileRepository profiles) {
        this.profiles = profiles;
    }

    @Transactional(readOnly = true)
    public Optional<EmployeeProfile> find(Long userId) {
        return profiles.findByUserId(userId);
    }

    /** Kayitli profil varsa onu, yoksa bos bir form nesnesi dondurur. */
    @Transactional(readOnly = true)
    public EmployeeProfile findOrEmpty(Long userId) {
        return profiles.findByUserId(userId).orElseGet(() -> {
            EmployeeProfile empty = new EmployeeProfile();
            empty.setUserId(userId);
            return empty;
        });
    }

    @Transactional
    public EmployeeProfile save(Long userId, EmployeeProfile form) {
        EmployeeProfile target = profiles.findByUserId(userId).orElseGet(EmployeeProfile::new);

        target.setUserId(userId);
        target.setFirstName(trim(form.getFirstName()));
        target.setLastName(trim(form.getLastName()));
        target.setNationalId(trim(form.getNationalId()));
        target.setBirthDate(form.getBirthDate());
        target.setPhone(trim(form.getPhone()));
        target.setAddress(trim(form.getAddress()));
        target.setCity(trim(form.getCity()));
        target.setMaritalStatus(trim(form.getMaritalStatus()));
        target.setBloodType(trim(form.getBloodType()));

        target.setCompanyName(trim(form.getCompanyName()));
        target.setDepartment(trim(form.getDepartment()));
        target.setPosition(trim(form.getPosition()));
        target.setManagerName(trim(form.getManagerName()));
        target.setWorkLocation(trim(form.getWorkLocation()));
        target.setEmploymentType(trim(form.getEmploymentType()));
        target.setStartDate(form.getStartDate());

        target.setSalary(form.getSalary());
        target.setCurrency(form.getCurrency() == null || form.getCurrency().isBlank() ? "TRY" : form.getCurrency());
        target.setBankName(trim(form.getBankName()));
        target.setIban(form.getIban() == null ? null : form.getIban().replace(" ", "").toUpperCase());

        target.setUpdatedAt(LocalDateTime.now());

        return profiles.save(target);
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
