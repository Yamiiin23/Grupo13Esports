package com.esports.resultservice.repository;

import com.esports.resultservice.model.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultadoRepository extends JpaRepository<Resultado, Long> {

    Optional<Resultado> findByPartidaId(Long partidaId);
    boolean existsByPartidaId(Long partidaId);
    List<Resultado> findByEstadoValidacion(Resultado.EstadoValidacion estado);
    List<Resultado> findByGanadorId(Long ganadorId);
}
