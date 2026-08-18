package com.vivek.gympulse.dto;
import com.vivek.gympulse.dto.DayScheduleRequest;

import lombok.Data;

@Data
public class ScheduleRequest {

    private DayScheduleRequest monday;
    private DayScheduleRequest tuesday;
    private DayScheduleRequest wednesday;
    private DayScheduleRequest thursday;
    private DayScheduleRequest friday;
    private DayScheduleRequest saturday;
    private DayScheduleRequest sunday;

}