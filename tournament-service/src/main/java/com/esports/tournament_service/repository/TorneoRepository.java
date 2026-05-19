package com.esports.tournament_service.repository;

import com.esports.tournament_service.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}