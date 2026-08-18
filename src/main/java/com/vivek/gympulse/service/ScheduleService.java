package com.vivek.gympulse.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivek.gympulse.dto.DayScheduleRequest;
import com.vivek.gympulse.dto.ScheduleRequest;
import com.vivek.gympulse.dto.TodayWorkoutResponse;
import com.vivek.gympulse.entity.ScheduledExercise;
import com.vivek.gympulse.entity.User;
import com.vivek.gympulse.entity.UserSchedule;
import com.vivek.gympulse.repository.ScheduledExerciseRepository;
import com.vivek.gympulse.repository.UserScheduleRepository;
import com.vivek.gympulse.security.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final UserScheduleRepository repository;

    private final ScheduledExerciseRepository exerciseRepository;

    private final CurrentUserService currentUserService;

    // ===========================
    // SAVE SCHEDULE
    // ===========================

    @Transactional
    public String saveSchedule(ScheduleRequest request) {

        

        User user = currentUserService.getCurrentUser();

        // Delete all child records first
        exerciseRepository.deleteByUserId(user.getId());

        // Then delete parent records
        repository.deleteByUserId(user.getId());


        repository.deleteByUserId(user.getId());

        List<UserSchedule> schedules = List.of(

                buildSchedule("Monday", request.getMonday(), user),
                buildSchedule("Tuesday", request.getTuesday(), user),
                buildSchedule("Wednesday", request.getWednesday(), user),
                buildSchedule("Thursday", request.getThursday(), user),
                buildSchedule("Friday", request.getFriday(), user),
                buildSchedule("Saturday", request.getSaturday(), user),
                buildSchedule("Sunday", request.getSunday(), user)

        );

        for (UserSchedule schedule : schedules) {

            System.out.println("--------------------------------");
            System.out.println("Day      : " + schedule.getDayName());
            System.out.println("Workout  : " + schedule.getWorkoutName());
            System.out.println("Exercises: " + schedule.getExercises());

            UserSchedule savedSchedule = repository.saveAndFlush(schedule);

            System.out.println("Saved ID      : " + savedSchedule.getId());
            System.out.println("Saved Workout : " + savedSchedule.getWorkoutName());

            List<ScheduledExercise> oldExercises =
                    exerciseRepository.findBySchedule(savedSchedule);

            if (!oldExercises.isEmpty()) {
                exerciseRepository.deleteAll(oldExercises);
            }

            List<ScheduledExercise> exerciseList =
                    schedule.getExercises()
                            .stream()
                            .map(name ->
                                    ScheduledExercise.builder()
                                            .exerciseName(name)
                                            .schedule(savedSchedule)
                                            .build()
                            )
                            .collect(Collectors.toList());

            exerciseRepository.saveAll(exerciseList);

        }

        return "Schedule Saved Successfully";
    }

    private UserSchedule buildSchedule(

            String day,

            DayScheduleRequest request,

            User user

    ) {

        return UserSchedule.builder()

                .dayName(day)

                .workoutName(request.getWorkout())

                .exercises(request.getExercises())

                .user(user)

                .build();

    }

    // ===========================
    // GET COMPLETE SCHEDULE
    // ===========================

    public List<UserSchedule> getSchedule() {

        User user = currentUserService.getCurrentUser();

        List<UserSchedule> schedules = repository.findByUser(user);

        for (UserSchedule schedule : schedules) {

            List<String> exercises = exerciseRepository

                    .findBySchedule(schedule)

                    .stream()

                    .map(ScheduledExercise::getExerciseName)

                    .collect(Collectors.toList());

            schedule.setExercises(exercises);

        }

        List<String> order = List.of(

                "Monday",

                "Tuesday",

                "Wednesday",

                "Thursday",

                "Friday",

                "Saturday",

                "Sunday"

        );

        schedules.sort(

                Comparator.comparingInt(

                        s -> order.indexOf(s.getDayName())

                )

        );

        return schedules;

    }

    // ===========================
    // TODAY'S WORKOUT
    // ===========================

    public TodayWorkoutResponse todayWorkout() {

        User user = currentUserService.getCurrentUser();

        DayOfWeek day = LocalDate.now().getDayOfWeek();

        String today = day.name().substring(0, 1)
                + day.name().substring(1).toLowerCase();

        UserSchedule schedule = repository

                .findByUserAndDayName(user, today)

                .orElseThrow(() ->
                        new RuntimeException("Schedule not found"));

        List<String> exercises = exerciseRepository

                .findBySchedule(schedule)

                .stream()

                .map(ScheduledExercise::getExerciseName)

                .collect(Collectors.toList());

        TodayWorkoutResponse response = new TodayWorkoutResponse();

        response.setDayName(today);

        response.setWorkoutName(schedule.getWorkoutName());

        response.setExercises(exercises);

        return response;

    }

}