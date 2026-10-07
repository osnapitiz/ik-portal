package com.pmt.ikportal.repository;

import com.pmt.ikportal.domain.LeaveRequest;
import com.pmt.ikportal.domain.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByUserIdOrderByStartDateDesc(Long userId);

    Optional<LeaveRequest> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndStatus(Long userId, LeaveStatus status);

    /** Tarih araligi kesisen, iptal/red disi talepler. */
    List<LeaveRequest> findByUserIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long userId, List<LeaveStatus> statuses, LocalDate endDate, LocalDate startDate);

    List<LeaveRequest> findByUserIdAndStatusInAndStartDateBetween(
            Long userId, List<LeaveStatus> statuses, LocalDate from, LocalDate to);
}
