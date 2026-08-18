package com.vivek.gympulse.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class WorkoutRequest {

    private String muscleGroup;

    private LocalDate workoutDate;

    private List<ExerciseRequest> exercises;

}