package com.esports.tournament_service.service;

import com.esports.tournament_service.dto.TorneoDTO;
import com.esports.tournament_service.exception.TorneoNotFoundException;
import com.esports.tournament_service.exception.TorneoValidationException;
import com.esports.tournament_service.exception.TorneoYaExisteException;
import com.esports.tournament_service.model.Torneo;
import com.esports.tournament_service.repository.TorneoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TorneoService {

    private static final Logger log = LoggerFactory.getLogger(TorneoService.class);
    private final TorneoRepository torneoRepository;

    public TorneoService(TorneoRepository torneoRepository) {
        this.torneoRepository = torneoRepository;
    }

    public TorneoDTO.Response crearTorneo(TorneoDTO.Request request) {
        log.info("[tournament-service] Intentando organizar torneo: {}", request.getNombre());

        if (torneoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new TorneoYaExisteException(request.getNombre());
        }

        if (request.getMaxParticipantes() == null || request.getMaxParticipantes() < 4) {
            throw new TorneoValidationException("Un torneo competitivo debe tener un máximo mínimo de 4 participantes.");
        }

        Torneo torneo = Torneo.builder()
                .nombre(request.getNombre())
                .gameId(request.getGameId())
                .maxParticipantes(request.getMaxParticipantes())
                .participantesActuales(0)
                .estado(Torneo.EstadoTorneo.INSCRIPCION)
                .build();

        Torneo guardado = torneoRepository.save(torneo);
        return TorneoDTO.Response.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public List<TorneoDTO.Response> listarTorneos() {
        log.info("[tournament-service] Solicitando listado de torneos activos");
        return torneoRepository.findAll().stream()
                .filter(t -> t.getEstado() != Torneo.EstadoTorneo.ANULADO)
                .map(TorneoDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TorneoDTO.Response buscarPorId(Long id) {
        return TorneoDTO.Response.fromEntity(obtenerTorneoActivo(id));
    }

    public TorneoDTO.Response actualizarTorneo(Long id, TorneoDTO.Request request) {
        Torneo torneo = obtenerTorneoActivo(id);

        if (torneo.getEstado() != Torneo.EstadoTorneo.INSCRIPCION) {
            throw new TorneoValidationException("No se pueden modificar las propiedades de un torneo que ya está " + torneo.getEstado());
        }

        if (!torneo.getNombre().equalsIgnoreCase(request.getNombre())
                && torneoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new TorneoYaExisteException(request.getNombre());
        }

        torneo.setNombre(request.getNombre());
        torneo.setGameId(request.getGameId());
        if(request.getMaxParticipantes() != null) torneo.setMaxParticipantes(request.getMaxParticipantes());

        return TorneoDTO.Response.fromEntity(torneoRepository.save(torneo));
    }

    public TorneoDTO.Response eliminarTorneoLogico(Long id, String justificacion) {
        if (justificacion == null || justificacion.isBlank()) {
            throw new TorneoValidationException("La justificación de anulación es obligatoria para la auditoría técnica.");
        }

        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new TorneoNotFoundException(id));

        if (torneo.getEstado() == Torneo.EstadoTorneo.EN_CURSO) {
            throw new TorneoValidationException("No se puede anular un torneo que se encuentra activamente EN_CURSO. Debe finalizarse o suspenderse formalmente.");
        }
        if (torneo.getEstado() == Torneo.EstadoTorneo.ANULADO) {
            throw new TorneoValidationException("El torneo ya se encuentra anulado lógicamente.");
        }

        torneo.setEstado(Torneo.EstadoTorneo.ANULADO);
        torneo.setMotivoAnulacion(justificacion);

        log.info("[tournament-service] Torneo ID={} dado de baja lógicamente de la base de datos.", id);
        return TorneoDTO.Response.fromEntity(torneoRepository.save(torneo));
    }

    public TorneoDTO.Response iniciarTorneo(Long id) {
        Torneo torneo = obtenerTorneoActivo(id);

        if (torneo.getEstado() != Torneo.EstadoTorneo.INSCRIPCION) {
            throw new TorneoValidationException("Solo se pueden iniciar torneos que estén en fase de INSCRIPCION.");
        }

        if (torneo.getParticipantesActuales() == null || torneo.getParticipantesActuales() < 4) {
            log.warn("[tournament-service] Falló algoritmo de inicio. Participantes insuficientes: {}", torneo.getParticipantesActuales());
            throw new TorneoValidationException("No se puede iniciar el torneo. Se requiere un mínimo técnico de 4 equipos inscritos (Inscritos actuales: " + torneo.getParticipantesActuales() + ").");
        }

        torneo.setEstado(Torneo.EstadoTorneo.EN_CURSO);
        log.info("[tournament-service] Algoritmo aprobado. Torneo ID={} pasa a estado EN_CURSO.", id);
        return TorneoDTO.Response.fromEntity(torneoRepository.save(torneo));
    }

    private Torneo obtenerTorneoActivo(Long id) {
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new TorneoNotFoundException(id));
        if (torneo.getEstado() == Torneo.EstadoTorneo.ANULADO) {
            throw new TorneoNotFoundException(id); // Ocultamos recursos anulados simulando la eliminación
        }
        return torneo;
    }
}