package com.portfolio.gymmanager.repository;

import com.portfolio.gymmanager.model.DailyRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyRoutineRepository extends JpaRepository<DailyRoutine, Integer> {
}
