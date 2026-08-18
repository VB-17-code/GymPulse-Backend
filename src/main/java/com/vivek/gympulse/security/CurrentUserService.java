package com.vivek.gympulse.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vivek.gympulse.entity.User;
import lombok.RequiredArgsConstructor;
import com.vivek.gympulse.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository repository;

    public User getCurrentUser() {

        String email = SecurityContextHolder

                .getContext()

                .getAuthentication()

                .getName();

        return repository.findByEmail(email)

                .orElseThrow(

                        () -> new RuntimeException("User not found")

                );

    }

}