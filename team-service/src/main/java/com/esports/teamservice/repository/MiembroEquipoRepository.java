package com.esports.teamservice.repository;

import com.esports.teamservice.model.MiembroEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MiembroEquipoRepository extends JpaRepository<MiembroEquipo, Long> {

    boolean existsByEquipoIdAndUsuarioId(Long equipoId, Long usuarioId);

    List<MiembroEquipo> findByEquipoId(Long equipoId);

    List<MiembroEquipo> findByUsuarioId(Long usuarioId);
}
