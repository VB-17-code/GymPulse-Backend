package com.vivek.gympulse.dto;

import java.util.List;

public class TodayWorkoutResponse {

    private String dayName;
    private String workoutName;
    private List<String> exercises;

    public TodayWorkoutResponse() {
    }

    public TodayWorkoutResponse(String dayName, String workoutName, List<String> exercises) {
        this.dayName = dayName;
        this.workoutName = workoutName;
        this.exercises = exercises;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public List<String> getExercises() {
        return exercises;
    }

    public void setExercises(List<String> exercises) {
        this.exercises = exercises;
    }
}