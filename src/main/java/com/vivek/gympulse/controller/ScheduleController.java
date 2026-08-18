package com.vivek.gympulse.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivek.gympulse.dto.ScheduleRequest;
import com.vivek.gympulse.dto.TodayWorkoutResponse;
import com.vivek.gympulse.entity.UserSchedule;
import com.vivek.gympulse.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService service;

    @PostMapping("/create")
public String create(@RequestBody ScheduleRequest request) {

    System.out.println("CONTROLLER HIT");

    return service.saveSchedule(request);
}

    @GetMapping
    public ResponseEntity<List<UserSchedule>> getSchedule() {
        return ResponseEntity.ok(service.getSchedule());
    }

    @GetMapping("/today")
    public TodayWorkoutResponse today() {
        return service.todayWorkout();
    }
}