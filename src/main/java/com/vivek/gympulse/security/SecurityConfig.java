package com.vivek.gympulse.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

       // configuration.setAllowedOrigins(
         //       List.of("http://localhost:5173")
        //);

        configuration.setAllowedOrigins(
    List.of(
        "http://localhost:5173",
        "http://localhost:5174"
    )
);

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {

    http

        // Enable CORS
        .cors(Customizer.withDefaults())

        // Disable CSRF because we use JWT
        .csrf(csrf -> csrf.disable())

        // Stateless JWT authentication
        .sessionManagement(session ->
                session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                )
        )

        .authorizeHttpRequests(auth -> auth

                // Allow CORS preflight requests
                .requestMatchers(
                        org.springframework.http.HttpMethod.OPTIONS,
                        "/**"
                )
                .permitAll()

                // Public authentication endpoints
                .requestMatchers(
                        "/auth/login",
                        "/auth/signup",
                        "/auth/forgot-password",
                        "/auth/verify-otp",
                        "/auth/reset-password"
                )
                .permitAll()

                // Everything else requires JWT
                .anyRequest()
                .authenticated()
        )

        // JWT filter
        .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

    return http.build();

    }

}