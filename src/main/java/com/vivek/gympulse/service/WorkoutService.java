package com.vivek.gympulse.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vivek.gympulse.dto.DashboardResponse;
import com.vivek.gympulse.dto.ExerciseRequest;
import com.vivek.gympulse.dto.SetRequest;
import com.vivek.gympulse.dto.WorkoutRequest;

import com.vivek.gympulse.entity.Exercise;
import com.vivek.gympulse.entity.ExerciseSet;
import com.vivek.gympulse.entity.User;
import com.vivek.gympulse.entity.WorkoutSession;

import com.vivek.gympulse.repository.ExerciseRepository;
import com.vivek.gympulse.repository.ExerciseSetRepository;
import com.vivek.gympulse.repository.UserRepository;
import com.vivek.gympulse.repository.WorkoutSessionRepository;

import com.vivek.gympulse.security.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutService {


    private final WorkoutSessionRepository sessionRepository;

    private final ExerciseRepository exerciseRepository;

    private final ExerciseSetRepository setRepository;

    private final CurrentUserService currentUserService;

    // methods...
    private final UserRepository userRepository;

    // ===========================
    // SAVE WORKOUT
    // ===========================

    public String saveWorkout(WorkoutRequest request) {

    User user = currentUserService.getCurrentUser();

    WorkoutSession session = WorkoutSession.builder()
        .muscleGroup(request.getMuscleGroup())
        .workoutDate(
                request.getWorkoutDate() != null
                        ? request.getWorkoutDate()
                        : LocalDate.now()
        )
        .user(user)
        .build();

    sessionRepository.save(session);

    for (ExerciseRequest exerciseDTO : request.getExercises()) {

        Exercise exercise = Exercise.builder()
                .exerciseName(exerciseDTO.getExerciseName())
                .session(session)
                .build();

        exerciseRepository.save(exercise);

        int setNumber = 1;

        for (SetRequest setDTO : exerciseDTO.getSets()) {

            ExerciseSet set = ExerciseSet.builder()
                    .weight(setDTO.getWeight())
                    .reps(setDTO.getReps())
                    .setNumber(setNumber++)
                    .exercise(exercise)
                    .build();

            setRepository.save(set);
        }
    }

    return "Workout Saved Successfully";
}

    // ===========================
// HISTORY
// ===========================

public List<WorkoutSession> history() {

    User user = currentUserService.getCurrentUser();

    return sessionRepository.findByUserOrderByWorkoutDateDesc(user);
}


// ===========================
// WEEKLY VOLUME
// ===========================

public Double weeklyVolume() {

    User user = currentUserService.getCurrentUser();

    List<WorkoutSession> sessions = sessionRepository.findByUser(user);

    LocalDate weekAgo = LocalDate.now().minusDays(7);

    double total = 0;

    for (WorkoutSession session : sessions) {

        if (session.getWorkoutDate().isAfter(weekAgo)
                || session.getWorkoutDate().isEqual(weekAgo)) {

            for (Exercise exercise : session.getExercises()) {

                for (ExerciseSet set : exercise.getSets()) {

                    total += set.getWeight() * set.getReps();

                }

            }

        }

    }

    return total;

}


// ===========================
// MONTHLY VOLUME
// ===========================

public Double monthlyVolume() {

    User user = currentUserService.getCurrentUser();

    List<WorkoutSession> sessions = sessionRepository.findByUser(user);

    LocalDate monthAgo = LocalDate.now().minusMonths(1);

    double total = 0;

    for (WorkoutSession session : sessions) {

        if (session.getWorkoutDate().isAfter(monthAgo)
                || session.getWorkoutDate().isEqual(monthAgo)) {

            for (Exercise exercise : session.getExercises()) {

                for (ExerciseSet set : exercise.getSets()) {

                    total += set.getWeight() * set.getReps();

                }

            }

        }

    }

    return total;

}


// ===========================
// STREAK
// ===========================

public Integer streak() {

    User user = currentUserService.getCurrentUser();

    List<WorkoutSession> sessions = sessionRepository.findByUser(user);

    Set<LocalDate> dates = sessions.stream()
            .map(WorkoutSession::getWorkoutDate)
            .collect(Collectors.toSet());

    LocalDate today = LocalDate.now();

    int streak = 0;

    while (dates.contains(today)) {

        streak++;

        today = today.minusDays(1);

    }

    return streak;

}

    // ===========================
    // DASHBOARD
    // ===========================

    public DashboardResponse dashboard() {

    User user = currentUserService.getCurrentUser();

    Integer totalWorkouts =
            Math.toIntExact(sessionRepository.countByUser(user));

    Double weekly = weeklyVolume();

    Double monthly = monthlyVolume();

    Integer streak = streak();

    double best = 0;

    List<WorkoutSession> sessions = sessionRepository.findByUser(user);

    for (WorkoutSession session : sessions) {

        for (Exercise exercise : session.getExercises()) {

            for (ExerciseSet set : exercise.getSets()) {

                if (set.getWeight() > best) {
                    best = set.getWeight();
                }

            }

        }

    }

    return DashboardResponse.builder()
            .totalWorkouts(totalWorkouts)
            .weeklyVolume(weekly)
            .monthlyVolume(monthly)
            .bestLift(best)
            .streak(streak)
            .build();
}

        // ===========================
    // RECENT WORKOUTS
    // ===========================

        // ===========================
    // RECENT WORKOUTS
    // ===========================

    public List<WorkoutSession> recent() {

        User user = currentUserService.getCurrentUser();

        return sessionRepository
                .findByUserOrderByWorkoutDateDesc(user)
                .stream()
                .limit(5)
                .toList();

    }

    // ===========================
    // EXERCISE PROGRESS
    // ===========================

    public List<Exercise> exerciseProgress(String exerciseName) {

        User user = currentUserService.getCurrentUser();

return exerciseRepository
        .findBySessionUserAndExerciseNameOrderBySessionWorkoutDateAsc(
                user,
                exerciseName
        );

    }

    // ===========================
    // TOTAL VOLUME OF EXERCISE
    // ===========================

    public Double calculateVolume(Long exerciseId) {

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        double total = 0;

        for (ExerciseSet set : exercise.getSets()) {

            total += set.getWeight() * set.getReps();

        }

        return total;

    }

    // ===========================
    // PERSONAL RECORD
    // ===========================

    public Double pr(Long exerciseId) {

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        double max = 0;

        for (ExerciseSet set : exercise.getSets()) {

            if (set.getWeight() > max) {

                max = set.getWeight();

            }

        }

        return max;

    }

}