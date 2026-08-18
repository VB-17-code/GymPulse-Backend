package com.vivek.gympulse.auth.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {

    private String email;

    private String otp;
}