package com.vivek.gympulse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vivek.gympulse.entity.Exercise;
import com.vivek.gympulse.entity.User;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByExerciseName(String exerciseName);

    List<Exercise> findBySessionUserAndExerciseNameOrderBySessionWorkoutDateAsc(
            User user,
            String exerciseName
    );

}