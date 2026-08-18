package com.vivek.gympulse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vivek.gympulse.entity.User;
import com.vivek.gympulse.entity.UserSchedule;

import jakarta.transaction.Transactional;

public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {

    List<UserSchedule> findByUser(User user);

    Optional<UserSchedule> findByUserAndDayName(User user, String dayName);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserSchedule u WHERE u.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}