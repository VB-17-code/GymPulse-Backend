package com.vivek.gympulse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vivek.gympulse.entity.ScheduledExercise;
import com.vivek.gympulse.entity.UserSchedule;

import jakarta.transaction.Transactional;

public interface ScheduledExerciseRepository
        extends JpaRepository<ScheduledExercise, Long> {

    List<ScheduledExercise> findBySchedule(UserSchedule schedule);

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM ScheduledExercise se
        WHERE se.schedule.user.id = :userId
    """)
    void deleteByUserId(@Param("userId") Long userId);

}