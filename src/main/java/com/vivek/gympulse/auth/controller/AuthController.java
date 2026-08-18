package com.vivek.gympulse.auth.controller;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vivek.gympulse.auth.dto.ChangePasswordRequest;
import com.vivek.gympulse.auth.dto.ForgotPasswordRequest;
import com.vivek.gympulse.auth.dto.VerifyOtpRequest;
import com.vivek.gympulse.auth.dto.ResetPasswordRequest;

import com.vivek.gympulse.auth.dto.AuthResponse;
import com.vivek.gympulse.auth.dto.LoginRequest;
import com.vivek.gympulse.auth.dto.SignupRequest;
import com.vivek.gympulse.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Valid
            @RequestBody
            SignupRequest request
    ) {

        try {

            String message = service.signup(request);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            message
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );

        }

    }


    @PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(
        @RequestBody ForgotPasswordRequest request
) {

    try {

        String message =
                service.sendPasswordResetOtp(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        message
                )
        );

    } catch (RuntimeException e) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "message",
                                e.getMessage()
                        )
                );
    }
}

    @PostMapping("/login")
    public ResponseEntity<?> login(

            @Valid

            @RequestBody

            LoginRequest request

    ) {

        try {

            return ResponseEntity.ok(

                    service.login(

                            request

                    )

            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(

                            Map.of(

                                    "message",

                                    e.getMessage()

                            )

                    );

        }

    }

    @PostMapping("/change-password")
public ResponseEntity<?> changePassword(

        @RequestBody ChangePasswordRequest request

) {

    try {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();


        String message =
                service.changePassword(
                        email,
                        request
                );


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        message
                )
        );

    }

    catch (RuntimeException e) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "message",
                                e.getMessage()
                        )
                );

    }

}

@PostMapping("/verify-otp")
public ResponseEntity<?> verifyOtp(
        @RequestBody VerifyOtpRequest request
) {

    try {

        String message =
                service.verifyPasswordResetOtp(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        message
                )
        );

    } catch (RuntimeException e) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "message",
                                e.getMessage()
                        )
                );
    }
}

@PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(
        @RequestBody ResetPasswordRequest request
) {

    try {

        String message =
                service.resetPassword(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        message
                )
        );

    } catch (RuntimeException e) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "message",
                                e.getMessage()
                        )
                );
    }
}

}