package com.esports.registration_service.service;

import com.esports.registration_service.dto.InscripcionDTO;
import com.esports.registration_service.exception.InscripcionDuplicadaException;
import com.esports.registration_service.exception.InscripcionNotFoundException;
import com.esports.registration_service.exception.InscripcionValidationException;
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

        boolean yaExiste = inscripcionRepository.findByTorneoId(request.getTorneoId()).stream()
                .anyMatch(i -> i.getEquipoId().equals(request.getEquipoId()) && i.getEstado() != Inscripcion.EstadoInscripcion.CANCELADA);

        if (yaExiste) {
            log.warn("[registration-service] Conflicto de negocio: El equipo ya tiene una solicitud vigente para este torneo.");
            throw new InscripcionDuplicadaException(request.getTorneoId(), request.getEquipoId());
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .torneoId(request.getTorneoId())
                .equipoId(request.getEquipoId())
                .estado(Inscripcion.EstadoInscripcion.PENDIENTE)
                .build();

        Inscripcion guardada = inscripcionRepository.save(inscripcion);
        return InscripcionDTO.Response.fromEntity(guardada);
    }

    @Transactional(readOnly = true)
    public List<InscripcionDTO.Response> listarInscripciones(Long torneoId) {
        log.info("[registration-service] Solicitando listado de inscripciones activas");
        List<Inscripcion> inscripciones = (torneoId != null)
                ? inscripcionRepository.findByTorneoId(torneoId)
                : inscripcionRepository.findAll();

        return inscripciones.stream()
                .map(InscripcionDTO.Response::fromEntity)
                .toList();
    }

    public InscripcionDTO.Response actualizarEstado(Long id, InscripcionDTO.UpdateStatusRequest request) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new InscripcionNotFoundException(id));

        if (inscripcion.getEstado() == Inscripcion.EstadoInscripcion.CANCELADA) {
            throw new InscripcionValidationException("No se puede cambiar el estado de una inscripción que ya fue CANCELADA por el competidor.");
        }

        try {
            Inscripcion.EstadoInscripcion nuevoEstado = Inscripcion.EstadoInscripcion.valueOf(request.getEstado().toUpperCase());
            inscripcion.setEstado(nuevoEstado);
        } catch (IllegalArgumentException e) {
            throw new InscripcionValidationException("Estado inválido. Los permitidos son de tipo: PENDIENTE, ACEPTADA o RECHAZADA");
        }

        return InscripcionDTO.Response.fromEntity(inscripcionRepository.save(inscripcion));
    }

    public InscripcionDTO.Response cancelarInscripcionLogica(Long id, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new InscripcionValidationException("El motivo de cancelación es mandatorio para resguardar la trazabilidad.");
        }

        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new InscripcionNotFoundException(id));

        if (inscripcion.getEstado() == Inscripcion.EstadoInscripcion.CANCELADA) {
            throw new InscripcionValidationException("La solicitud de inscripción ya se encuentra cancelada.");
        }

        inscripcion.setEstado(Inscripcion.EstadoInscripcion.CANCELADA);
        inscripcion.setMotivoCancelacion(motivo);

        log.info("[registration-service] Inscripción ID={} dada de baja de forma lógica del sistema.", id);
        return InscripcionDTO.Response.fromEntity(inscripcionRepository.save(inscripcion));
    }
}