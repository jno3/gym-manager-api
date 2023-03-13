package com.portfolio.gymmanager.repository;

import com.portfolio.gymmanager.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<List<Client>> findByGym_id(Integer gymId);
}
