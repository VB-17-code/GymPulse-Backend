package com.vivek.gympulse.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final Key key;


    public JwtService(
            @Value("${JWT_SECRET}") String secret
    ) {

        if (secret == null || secret.length() < 32) {

            throw new IllegalArgumentException(
                    "JWT_SECRET must be at least 32 characters long"
            );
        }

        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }


    // =====================================================
    // GENERATE JWT
    // =====================================================

    public String generateToken(String email) {

        return Jwts.builder()

                .subject(email)

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 86400000
                        )
                )

                .signWith(key)

                .compact();
    }


    // =====================================================
    // EXTRACT EMAIL
    // =====================================================

    public String extractEmail(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    // =====================================================
    // EXTRACT EXPIRATION
    // =====================================================

    public Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }


    // =====================================================
    // GENERIC CLAIM EXTRACTOR
    // =====================================================

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        Claims claims =
                Jwts.parser()

                        .verifyWith(
                                (javax.crypto.SecretKey) key
                        )

                        .build()

                        .parseSignedClaims(token)

                        .getPayload();


        return resolver.apply(claims);
    }


    // =====================================================
    // VALIDATE TOKEN
    // =====================================================

    public boolean isTokenValid(
            String token,
            String email
    ) {

        String extracted =
                extractEmail(token);

        return extracted.equals(email)
                && extractExpiration(token)
                        .after(new Date());
    }

}