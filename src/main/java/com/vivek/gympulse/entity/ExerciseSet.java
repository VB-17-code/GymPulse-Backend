package com.vivek.gympulse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "exercise_sets")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer setNumber;

    private Double weight;

    private Integer reps;

    @ManyToOne
    @JoinColumn(name = "exercise_id")

    @JsonIgnore
    private Exercise exercise;

}