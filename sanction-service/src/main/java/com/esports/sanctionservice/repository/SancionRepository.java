package com.esports.sanctionservice.repository;

import com.esports.sanctionservice.model.Sancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {

    List<Sancion> findByUsuarioId(Long usuarioId);
    List<Sancion> findByEquipoId(Long equipoId);
    List<Sancion> findByEstado(Sancion.EstadoSancion estado);
    List<Sancion> findByUsuarioIdAndEstado(Long usuarioId, Sancion.EstadoSancion estado);
    List<Sancion> findByEquipoIdAndEstado(Long equipoId, Sancion.EstadoSancion estado);

    @Query("SELECT s FROM Sancion s WHERE s.usuarioId = :usuarioId " +
           "AND s.estado = 'ACTIVA' " +
           "AND s.severidad <> 'ADVERTENCIA' " +
           "AND s.fechaInicio <= :ahora AND s.fechaFin > :ahora")
    List<Sancion> findSancionesBloqueantesUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("ahora") LocalDateTime ahora);


    @Query("SELECT s FROM Sancion s WHERE s.equipoId = :equipoId " +
           "AND s.estado = 'ACTIVA' " +
           "AND s.severidad <> 'ADVERTENCIA' " +
           "AND s.fechaInicio <= :ahora AND s.fechaFin > :ahora")
    List<Sancion> findSancionesBloqueantesEquipo(
            @Param("equipoId") Long equipoId,
            @Param("ahora") LocalDateTime ahora);

    @Query("SELECT s FROM Sancion s WHERE s.estado = 'ACTIVA' AND s.fechaFin < :ahora")
    List<Sancion> findSancionesVencidas(@Param("ahora") LocalDateTime ahora);
}
