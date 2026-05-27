package com.esports.registration_service.repository;

import com.esports.registration_service.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByTorneoId(Long torneoId);
    boolean existsByTorneoIdAndEquipoId(Long torneoId, Long equipoId);
}