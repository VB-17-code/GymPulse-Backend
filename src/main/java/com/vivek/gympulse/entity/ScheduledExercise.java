package com.vivek.gympulse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "scheduled_exercises")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String exerciseName;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "schedule_id")

    @JsonIgnore
    private UserSchedule schedule;

}