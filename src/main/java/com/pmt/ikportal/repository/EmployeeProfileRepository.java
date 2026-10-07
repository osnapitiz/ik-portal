package com.pmt.ikportal.repository;

import com.pmt.ikportal.domain.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {

    Optional<EmployeeProfile> findByUserId(Long userId);
}
