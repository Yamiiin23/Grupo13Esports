package com.esports.registration_service.service;

import com.esports.registration_service.dto.InscripcionDTO;
import com.esports.registration_service.exception.InscripcionDuplicadaException;
import com.esports.registration_service.model.Inscripcion;
import com.esports.registration_service.repository.InscripcionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InscripcionService {

    private static final Logger log = LoggerFactory.getLogger(InscripcionService.class);
    private final InscripcionRepository inscripcionRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    public InscripcionDTO.Response registrarEquipo(InscripcionDTO.Request request) {
        log.info("[registration-service] Procesando inscripción: Equipo ID={} en Torneo ID={}", request.getEquipoId(), request.getTorneoId());

        if (inscripcionRepository.existsByTorneoIdAndEquipoId(request.getTorneoId(), request.getEquipoId())) {
            log.warn("[registration-service] Conflicto: El equipo ya pertenece a este torneo.");
            throw new InscripcionDuplicadaException(request.getTorneoId(), request.getEquipoId());
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .torneoId(request.getTorneoId())
                .equipoId(request.getEquipoId())
                .estado(Inscripcion.EstadoInscripcion.PENDIENTE)
                .build();

        Inscripcion guardada = inscripcionRepository.save(inscripcion);
        log.info("[registration-service] Inscripción creada con éxito. ID={}, Estado={}", guardada.getId(), guardada.getEstado());

        return InscripcionDTO.Response.fromEntity(guardada);
    }

    @Transactional(readOnly = true)
    public List<InscripcionDTO.Response> listarInscripciones(Long torneoId) {
        log.info("[registration-service] Listando inscripciones del sistema.");
        List<Inscripcion> inscripciones = (torneoId != null)
                ? inscripcionRepository.findByTorneoId(torneoId)
                : inscripcionRepository.findAll();

        return inscripciones.stream()
                .map(InscripcionDTO.Response::fromEntity)
                .toList();
    }

    public InscripcionDTO.Response actualizarEstado(Long id, InscripcionDTO.UpdateStatusRequest request) {
        log.info("[registration-service] Actualizando estado de la inscripción ID={}", id);

        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new com.esports.registration_service.exception.InscripcionNotFoundException(id));

        try {
            Inscripcion.EstadoInscripcion nuevoEstado = Inscripcion.EstadoInscripcion.valueOf(request.getEstado().toUpperCase());
            inscripcion.setEstado(nuevoEstado);
        } catch (IllegalArgumentException e) {
            log.error("[registration-service] Estado invocado inválido: {}", request.getEstado());
            throw new IllegalArgumentException("Estado inválido. Los permitidos son: PENDIENTE, ACEPTADA o RECHAZADA");
        }

        Inscripcion actualizada = inscripcionRepository.save(inscripcion);
        log.info("[registration-service] Inscripción ID={} guardada con estado: {}", actualizada.getId(), actualizada.getEstado());

        return InscripcionDTO.Response.fromEntity(actualizada);
    }

    public void cancelarInscripcion(Long id) {
        log.info("[registration-service] Removiendo la inscripción ID={}", id);
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new com.esports.registration_service.exception.InscripcionNotFoundException(id));
        inscripcionRepository.delete(inscripcion);
        log.info("[registration-service] Inscripción ID={} eliminada correctamente.", id);
    }
}