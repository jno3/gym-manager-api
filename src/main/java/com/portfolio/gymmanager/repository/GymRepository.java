package com.portfolio.gymmanager.repository;

import com.portfolio.gymmanager.model.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GymRepository extends JpaRepository<Gym, Integer> {
}
