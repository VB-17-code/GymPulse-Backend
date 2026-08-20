package com.vivek.gympulse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(
            @Value("${RESEND_API_KEY}") String apiKey
    ) {
        this.resend = new Resend(apiKey);
    }

    public void sendOtpEmail(
            String email,
            String otp
    ) {

        CreateEmailOptions emailOptions =
                CreateEmailOptions.builder()
                        .from("GymPulse <onboarding@resend.dev>")
                        .to(email)
                        .subject("GymPulse - Password Reset OTP")
                        .html(
                                "<h2>GymPulse Password Reset</h2>"
                                + "<p>Hello,</p>"
                                + "<p>Your GymPulse password reset OTP is:</p>"
                                + "<h1>" + otp + "</h1>"
                                + "<p>This OTP is valid for <b>5 minutes</b>.</p>"
                                + "<p>If you did not request a password reset, "
                                + "please ignore this email.</p>"
                                + "<br>"
                                + "<p>Regards,<br>GymPulse Team</p>"
                        )
                        .build();

        try {

            resend.emails().send(emailOptions);

        } catch (ResendException e) {

            throw new RuntimeException(
                    "Failed to send OTP email: " + e.getMessage(),
                    e
            );

        }
    }
}