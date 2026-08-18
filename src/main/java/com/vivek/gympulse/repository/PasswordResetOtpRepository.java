package com.vivek.gympulse.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vivek.gympulse.entity.PasswordResetOtp;

public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp>
    findTopByEmailOrderByIdDesc(String email);

    void deleteByEmail(String email);
}