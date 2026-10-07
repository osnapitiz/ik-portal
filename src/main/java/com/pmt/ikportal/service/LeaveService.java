package com.pmt.ikportal.service;

import com.pmt.ikportal.domain.EmployeeProfile;
import com.pmt.ikportal.domain.LeaveRequest;
import com.pmt.ikportal.domain.LeaveStatus;
import com.pmt.ikportal.repository.LeaveRequestRepository;
import com.pmt.ikportal.web.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {

    private static final List<LeaveStatus> BLOCKING_STATUSES =
            List.of(LeaveStatus.BEKLIYOR, LeaveStatus.ONAYLANDI);

    private final LeaveRequestRepository leaves;
    private final ProfileService profiles;

    public LeaveService(LeaveRequestRepository leaves, ProfileService profiles) {
        this.leaves = leaves;
        this.profiles = profiles;
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> list(Long userId) {
        return leaves.findByUserIdOrderByStartDateDesc(userId);
    }

    @Transactional
    public LeaveRequest create(Long userId, LeaveRequest form) {
        LocalDate start = form.getStartDate();
        LocalDate end = form.getEndDate();

        if (end.isBefore(start)) {
            throw new BusinessException("İzin bitiş tarihi, başlangıç tarihinden önce olamaz.");
        }
        if (ChronoUnit.DAYS.between(start, end) > 365) {
            throw new BusinessException("Tek bir talep en fazla 365 gün olabilir.");
        }
        if (!leaves.findByUserIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                userId, BLOCKING_STATUSES, end, start).isEmpty()) {
            throw new BusinessException("Bu tarih aralığında zaten bir izin talebiniz bulunuyor.");
        }

        int workDays = countWorkDays(start, end);
        if (workDays == 0) {
            throw new BusinessException("Seçilen aralıkta iş günü bulunmuyor (tamamı hafta sonuna denk geliyor).");
        }

        LeaveRequest request = new LeaveRequest();
        request.setUserId(userId);
        request.setLeaveType(form.getLeaveType());
        request.setStartDate(start);
        request.setEndDate(end);
        request.setWorkDays(workDays);
        request.setTotalDays((int) ChronoUnit.DAYS.between(start, end) + 1);
        request.setDescription(form.getDescription() == null || form.getDescription().isBlank()
                ? null : form.getDescription().trim());
        request.setStatus(LeaveStatus.BEKLIYOR);
        request.setCreatedAt(LocalDateTime.now());

        return leaves.save(request);
    }

    @Transactional
    public void cancel(Long userId, Long leaveId) {
        LeaveRequest request = leaves.findByIdAndUserId(leaveId, userId)
                .orElseThrow(() -> new BusinessException("İzin talebi bulunamadı."));

        if (request.getStatus() != LeaveStatus.BEKLIYOR) {
            throw new BusinessException("Yalnızca onay bekleyen talepler iptal edilebilir.");
        }
        request.setStatus(LeaveStatus.IPTAL);
        request.setDecidedAt(LocalDateTime.now());
        leaves.save(request);
    }

    /** Tek kullanicili demo akisi: talebi yonetici yerine kullanici sonuclandirabilir. */
    @Transactional
    public void decide(Long userId, Long leaveId, LeaveStatus decision) {
        if (decision != LeaveStatus.ONAYLANDI && decision != LeaveStatus.REDDEDILDI) {
            throw new BusinessException("Geçersiz karar.");
        }
        LeaveRequest request = leaves.findByIdAndUserId(leaveId, userId)
                .orElseThrow(() -> new BusinessException("İzin talebi bulunamadı."));

        if (request.getStatus() != LeaveStatus.BEKLIYOR) {
            throw new BusinessException("Bu talep zaten sonuçlandırılmış.");
        }
        request.setStatus(decision);
        request.setDecidedAt(LocalDateTime.now());
        leaves.save(request);
    }

    @Transactional(readOnly = true)
    public LeaveBalance balance(Long userId) {
        Optional<EmployeeProfile> profile = profiles.find(userId);
        if (profile.isEmpty() || profile.get().getStartDate() == null) {
            return LeaveBalance.unknown();
        }

        EmployeeProfile p = profile.get();
        LocalDate today = LocalDate.now();
        int seniority = Period.between(p.getStartDate(), today).getYears();
        int age = p.getBirthDate() == null ? 30 : Period.between(p.getBirthDate(), today).getYears();
        int entitled = entitlement(seniority, age);

        LocalDate yearStart = LocalDate.of(today.getYear(), 1, 1);
        LocalDate yearEnd = LocalDate.of(today.getYear(), 12, 31);

        List<LeaveRequest> thisYear = leaves.findByUserIdAndStatusInAndStartDateBetween(
                userId, BLOCKING_STATUSES, yearStart, yearEnd);

        int used = thisYear.stream()
                .filter(l -> l.getLeaveType().isDeductedFromAnnualBalance())
                .filter(l -> l.getStatus() == LeaveStatus.ONAYLANDI)
                .mapToInt(LeaveRequest::getWorkDays)
                .sum();

        int pending = thisYear.stream()
                .filter(l -> l.getLeaveType().isDeductedFromAnnualBalance())
                .filter(l -> l.getStatus() == LeaveStatus.BEKLIYOR)
                .mapToInt(LeaveRequest::getWorkDays)
                .sum();

        return new LeaveBalance(entitled, used, pending, seniority, seniority >= 1);
    }

    /** 4857 sayili Is Kanunu m.53 uyarinca kidem bazli yillik izin gun sayisi. */
    private int entitlement(int seniorityYears, int age) {
        int days;
        if (seniorityYears < 1) {
            days = 0;
        } else if (seniorityYears <= 5) {
            days = 14;
        } else if (seniorityYears < 15) {
            days = 20;
        } else {
            days = 26;
        }
        // 18 yasindan kucuk veya 50 yasindan buyuk calisanlar icin taban 20 gundur.
        if (days > 0 && (age < 18 || age > 50)) {
            days = Math.max(days, 20);
        }
        return days;
    }

    /** Hafta sonlarini haric tutarak is gunu sayar. */
    public static int countWorkDays(LocalDate start, LocalDate end) {
        int count = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            DayOfWeek day = d.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                count++;
            }
        }
        return count;
    }
}
