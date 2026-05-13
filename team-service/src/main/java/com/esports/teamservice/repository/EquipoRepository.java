package com.esports.teamservice.repository;

import com.esports.teamservice.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<Equipo> findByEstado(Equipo.EstadoEquipo estado);
    List<Equipo> findByJuegoPrincipalId(Long juegoPrincipalId);
    List<Equipo> findByCapitanId(Long capitanId);
    List<Equipo> findByJuegoPrincipalIdAndEstado(Long juegoPrincipalId, Equipo.EstadoEquipo estado);
}
