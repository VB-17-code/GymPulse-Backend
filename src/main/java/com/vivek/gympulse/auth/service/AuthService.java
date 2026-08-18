package com.vivek.gympulse.auth.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivek.gympulse.auth.dto.AuthResponse;
import com.vivek.gympulse.auth.dto.ChangePasswordRequest;
import com.vivek.gympulse.auth.dto.ForgotPasswordRequest;
import com.vivek.gympulse.auth.dto.LoginRequest;
import com.vivek.gympulse.auth.dto.ResetPasswordRequest;
import com.vivek.gympulse.auth.dto.SignupRequest;
import com.vivek.gympulse.auth.dto.VerifyOtpRequest;

import com.vivek.gympulse.entity.PasswordResetOtp;
import com.vivek.gympulse.entity.User;

import com.vivek.gympulse.repository.PasswordResetOtpRepository;
import com.vivek.gympulse.repository.UserRepository;

import com.vivek.gympulse.security.JwtService;
import com.vivek.gympulse.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;

    private final PasswordEncoder encoder;

    private final JwtService jwtService;

    private final PasswordResetOtpRepository otpRepository;

    private final EmailService emailService;

    private final SecureRandom secureRandom =
            new SecureRandom();


    // =====================================================
    // SIGNUP
    // =====================================================

    public String signup(SignupRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();


        if (repository.findByEmail(email).isPresent()) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }


        User user = User.builder()

                .name(
                        request.getName()
                                .trim()
                )

                .email(email)

                .password(
                        encoder.encode(
                                request.getPassword()
                        )
                )

                .createdAt(
                        LocalDateTime.now()
                )

                .build();


        repository.save(user);


        return "User Registered Successfully";
    }


    // =====================================================
    // LOGIN
    // =====================================================

    public AuthResponse login(
            LoginRequest request
    ) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();


        User user = repository
                .findByEmail(email)

                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );


        if (!encoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException(
                    "Invalid Password"
            );
        }


        String token =
                jwtService.generateToken(
                        user.getEmail()
                );


        return AuthResponse.builder()

                .token(token)

                .name(user.getName())

                .email(user.getEmail())

                .message("Login successful")

                .build();
    }


    // =====================================================
    // CHANGE PASSWORD
    // Used when user is already logged in
    // =====================================================

    public String changePassword(
            String email,
            ChangePasswordRequest request
    ) {

        User user = repository
                .findByEmail(email)

                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );


        // Check current password

        if (!encoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }


        // Validate new password

        if (request.getNewPassword() == null ||
                request.getNewPassword()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "New password cannot be empty"
            );
        }


        if (request.getNewPassword().length() < 6) {

            throw new RuntimeException(
                    "New password must be at least 6 characters"
            );
        }


        // Encode new password

        user.setPassword(
                encoder.encode(
                        request.getNewPassword()
                )
        );


        repository.save(user);


        return "Password changed successfully";
    }


    // =====================================================
    // FORGOT PASSWORD
    // SEND OTP
    // =====================================================

    @Transactional
    public String sendPasswordResetOtp(
            ForgotPasswordRequest request
    ) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();


        // Check user exists

        repository.findByEmail(email)

                .orElseThrow(
                        () -> new RuntimeException(
                                "No account found with this email"
                        )
                );


        // Generate 6 digit OTP

        String otp = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );


        // Delete previous OTP

        otpRepository.deleteByEmail(email);


        // Create new OTP

        PasswordResetOtp resetOtp =
                PasswordResetOtp.builder()

                        .email(email)

                        .otp(otp)

                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(5)
                        )

                        .build();


        // Save OTP

        otpRepository.save(resetOtp);


        // Send OTP email

        emailService.sendOtpEmail(
                email,
                otp
        );


        return "OTP sent successfully";
    }


    // =====================================================
    // VERIFY OTP
    // =====================================================

    @Transactional
    public String verifyPasswordResetOtp(
            VerifyOtpRequest request
    ) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();


        String otp = request.getOtp()
                .trim();


        // Find latest OTP

        PasswordResetOtp resetOtp =
                otpRepository
                        .findTopByEmailOrderByIdDesc(email)

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "OTP not found"
                                )
                        );


        // Check expiry

        if (resetOtp.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            otpRepository.delete(resetOtp);

            throw new RuntimeException(
                    "OTP has expired"
            );
        }


        // Check OTP

        if (!resetOtp.getOtp().equals(otp)) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }


        return "OTP verified successfully";
    }


    // =====================================================
    // RESET PASSWORD
    // =====================================================

    @Transactional
    public String resetPassword(
            ResetPasswordRequest request
    ) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();


        String otp = request.getOtp()
                .trim();


        // Find latest OTP

        PasswordResetOtp resetOtp =
                otpRepository
                        .findTopByEmailOrderByIdDesc(email)

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "OTP not found"
                                )
                        );


        // Check OTP expiry

        if (resetOtp.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            otpRepository.delete(resetOtp);

            throw new RuntimeException(
                    "OTP has expired"
            );
        }


        // Verify OTP

        if (!resetOtp.getOtp().equals(otp)) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }


        // Validate new password

        if (request.getNewPassword() == null ||
                request.getNewPassword()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "New password cannot be empty"
            );
        }


        if (request.getNewPassword().length() < 6) {

            throw new RuntimeException(
                    "Password must be at least 6 characters"
            );
        }


        // Find user

        User user = repository
                .findByEmail(email)

                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );


        // Encode new password

        user.setPassword(
                encoder.encode(
                        request.getNewPassword()
                )
        );


        repository.save(user);


        // Delete OTP after successful reset

        otpRepository.delete(resetOtp);


        return "Password reset successfully";
    }

}