package com.vivek.gympulse.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vivek.gympulse.dto.ExerciseScheduleRequest;
import com.vivek.gympulse.entity.ScheduledExercise;
import com.vivek.gympulse.entity.User;
import com.vivek.gympulse.entity.UserSchedule;
import com.vivek.gympulse.repository.ScheduledExerciseRepository;
import com.vivek.gympulse.repository.UserScheduleRepository;
import com.vivek.gympulse.security.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleExerciseService {

    private final CurrentUserService currentUserService;

    private final UserScheduleRepository scheduleRepository;

    private final ScheduledExerciseRepository exerciseRepository;

    public String saveExercises(ExerciseScheduleRequest request) {

        User user = currentUserService.getCurrentUser();

        UserSchedule schedule = scheduleRepository
                .findByUserAndDayName(user, request.getDayName())
                .orElseThrow(() ->
                        new RuntimeException("Schedule not found"));

        exerciseRepository.deleteAll(
                exerciseRepository.findBySchedule(schedule)
        );

        List<ScheduledExercise> list = request
                .getExercises()
                .stream()
                .map(name -> ScheduledExercise.builder()
                        .exerciseName(name)
                        .schedule(schedule)
                        .build())
                .toList();

        exerciseRepository.saveAll(list);

        return "Exercises Saved Successfully";
    }
}