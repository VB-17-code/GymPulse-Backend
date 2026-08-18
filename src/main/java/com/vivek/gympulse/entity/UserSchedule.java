package com.vivek.gympulse.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Monday, Tuesday...
    @Column(nullable = false)
    private String dayName;

    // Chest + Triceps
    @Column(nullable = false)
    private String workoutName;

    // List of exercises for this workout
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "schedule_exercises",
            joinColumns = @JoinColumn(name = "schedule_id")
    )
    @Column(name = "exercise_name")
    private List<String> exercises;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

}