package com.vivek.gympulse.auth.service;

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
import com.vivek.gympulse.service.MubaroService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;

    private final PasswordEncoder encoder;

    private final JwtService jwtService;

    private final PasswordResetOtpRepository otpRepository;

    private final MubaroService mubaroService;


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
    // SEND OTP USING MUBARO
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

        // Remove any previous verification state

        otpRepository.deleteByEmail(email);

        /*
         * Mubaro generates the OTP and sends it
         * directly to the user's email.
         */
        try {

            mubaroService.sendOtp(email);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send OTP: "
                            + e.getMessage()
            );
        }

        return "OTP sent successfully";
    }


    // =====================================================
    // VERIFY OTP
    // MUBARO VERIFICATION
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

        // Check user exists

        repository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "No account found with this email"
                        )
                );

        /*
         * Mubaro verifies the OTP.
         *
         * Important:
         * Mubaro OTPs should not be verified twice.
         */
        try {

            mubaroService.verifyOtp(
                    email,
                    otp
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Invalid or expired OTP"
            );
        }

        /*
         * OTP is successfully verified.
         *
         * Store only a temporary VERIFIED marker
         * in our database.
         *
         * We do NOT store the actual OTP.
         */

        otpRepository.deleteByEmail(email);

        PasswordResetOtp verifiedState =
                PasswordResetOtp.builder()

                        .email(email)

                        .otp("VERIFIED")

                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(5)
                        )

                        .build();

        otpRepository.save(verifiedState);

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

        // =================================================
        // CHECK VERIFIED OTP STATE
        // =================================================

        PasswordResetOtp resetState =
                otpRepository
                        .findTopByEmailOrderByIdDesc(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Please verify OTP first"
                                )
                        );

        // Make sure OTP was actually verified

        if (!"VERIFIED".equals(
                resetState.getOtp()
        )) {

            throw new RuntimeException(
                    "Please verify OTP first"
            );
        }

        // Check verification expiry

        if (resetState.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            otpRepository.delete(resetState);

            throw new RuntimeException(
                    "OTP verification has expired. Please request a new OTP."
            );
        }


        // =================================================
        // VALIDATE NEW PASSWORD
        // =================================================

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


        // =================================================
        // FIND USER
        // =================================================

        User user = repository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );


        // =================================================
        // UPDATE PASSWORD
        // =================================================

        user.setPassword(
                encoder.encode(
                        request.getNewPassword()
                )
        );

        repository.save(user);


        // =================================================
        // DELETE VERIFIED STATE
        // Prevent reuse
        // =================================================

        otpRepository.delete(resetState);

        return "Password reset successfully";
    }

}