package com.vivek.gympulse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vivek.gympulse.entity.User;
import com.vivek.gympulse.entity.WorkoutSession;

public interface WorkoutSessionRepository
        extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findByUser(
            User user
    );

    List<WorkoutSession> findByUserOrderByWorkoutDateDesc(
            User user
    );

    long countByUser(
            User user
    );

}