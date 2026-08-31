package com.vivek.gympulse.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MubaroService {

    private final RestClient restClient;

    @Value("${MUBARO_API_KEY}")
    private String apiKey;

    public MubaroService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.mubaro.app")
                .build();
    }

    public void sendOtp(String email) {

        restClient.post()
                .uri("/api/otp-services/send-otp")
                .header("x-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "email", email
                ))
                .retrieve()
                .toBodilessEntity();
    }

    public void verifyOtp(String email, String otp) {

        restClient.post()
                .uri("/api/otp-services/verify-otp")
                .header("x-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "email", email,
                        "otp", otp
                ))
                .retrieve()
                .toBodilessEntity();
    }
}