package com.vivek.gympulse.controller;

import java.util.List;
import com.vivek.gympulse.dto.DashboardResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vivek.gympulse.dto.WorkoutRequest;
import com.vivek.gympulse.entity.WorkoutSession;
import com.vivek.gympulse.service.WorkoutService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService service;

    @PostMapping("/log")
    public String saveWorkout(

            @RequestBody WorkoutRequest request

    ) {

        return service.saveWorkout(

                

                request

        );

    }

    @GetMapping("/history")
    public List<WorkoutSession> history() {

        return service.history(

                

        );

    }
    @GetMapping("/volume/{id}")
public Double volume(

        @PathVariable

        Long id

){

    return service.calculateVolume(

            id

    );

}

@GetMapping(

"/pr/{id}"

)

public Double pr(

@PathVariable

Long id

){

return service.pr(

id

);

}

@GetMapping("/dashboard")
public DashboardResponse dashboard(){

    return service.dashboard(

            

    );

}

@GetMapping("/analytics/week")
public Double week(){

    return service.weeklyVolume(

            

    );

}

@GetMapping("/analytics/month")
public Double month(){

    return service.monthlyVolume(

            

    );

}

@GetMapping("/streak")
public Integer streak(){

    return service.streak(

            

    );

}

@GetMapping("/recent")
public List<WorkoutSession> recent(){

    return service.recent(

            

    );

}

}