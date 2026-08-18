package com.vivek.gympulse.controller;

import org.springframework.web.bind.annotation.*;

import com.vivek.gympulse.dto.ExerciseScheduleRequest;
import com.vivek.gympulse.service.ScheduleExerciseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleExerciseController {

    private final ScheduleExerciseService service;

    @PostMapping("/exercises")
    public String save(@RequestBody ExerciseScheduleRequest request) {

        return service.saveExercises(request);

    }
}