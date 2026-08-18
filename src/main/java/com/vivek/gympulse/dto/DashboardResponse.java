package com.vivek.gympulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private Integer totalWorkouts;

    private Double weeklyVolume;

    private Double monthlyVolume;

    private Double bestLift;

    private Integer streak;

}