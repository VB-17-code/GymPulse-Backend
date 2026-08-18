package com.vivek.gympulse.dto;

import java.util.List;

import lombok.Data;

@Data
public class DayScheduleRequest {

    private String workout;

    private List<String> exercises;

}