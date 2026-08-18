package com.vivek.gympulse.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "workout_sessions")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate workoutDate;

    private String muscleGroup;

    @ManyToOne
    @JoinColumn(name = "user_id")

    @JsonIgnore
    private User user;

    @OneToMany(
    mappedBy = "session",
    cascade = CascadeType.ALL,
    fetch = FetchType.EAGER
)
private List<Exercise> exercises;

}