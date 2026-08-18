package com.vivek.gympulse.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExerciseRequest {

    private String exerciseName;

    private List<SetRequest> sets;

}