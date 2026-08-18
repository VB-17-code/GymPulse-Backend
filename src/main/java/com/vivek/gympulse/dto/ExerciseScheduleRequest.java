package com.vivek.gympulse.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExerciseScheduleRequest {

    private String dayName;

    private List<String> exercises;

}