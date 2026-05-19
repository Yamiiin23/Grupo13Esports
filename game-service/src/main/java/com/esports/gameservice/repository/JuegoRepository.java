package com.esports.gameservice.repository;

import com.esports.gameservice.model.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {

    List<Juego> findByEstado(Juego.EstadoJuego estado);

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Juego> findByNombreIgnoreCase(String nombre);

    List<Juego> findByGeneroIgnoreCaseAndEstado(String genero, Juego.EstadoJuego estado);
}
