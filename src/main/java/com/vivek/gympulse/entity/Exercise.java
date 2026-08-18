package com.vivek.gympulse.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "exercises")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String exerciseName;

    @ManyToOne
    @JoinColumn(name = "session_id")

    @JsonIgnore
    private WorkoutSession session;

    @OneToMany(
    mappedBy = "exercise",
    cascade = CascadeType.ALL,
    fetch = FetchType.EAGER
)
private List<ExerciseSet> sets;

}