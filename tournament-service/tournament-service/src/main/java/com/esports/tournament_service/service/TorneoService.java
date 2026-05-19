package com.esports.tournament_service.service;

import com.esports.tournament_service.dto.TorneoDTO;
import com.esports.tournament_service.exception.TorneoNotFoundException;
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
            log.warn("[tournament-service] Nombre de torneo duplicado: {}", request.getNombre());
            throw new TorneoYaExisteException(request.getNombre());
        }

        Torneo torneo = Torneo.builder()
                .nombre(request.getNombre())
                .gameId(request.getGameId())
                .build();

        Torneo guardado = torneoRepository.save(torneo);
        log.info("[tournament-service] Torneo creado de forma exitosa. ID={}, Juego asignado ID={}", guardado.getId(), guardado.getGameId());

        return TorneoDTO.Response.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public List<TorneoDTO.Response> listarTorneos() {
        log.info("[tournament-service] Solicitando listado completo de torneos");
        return torneoRepository.findAll().stream()
                .map(TorneoDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TorneoDTO.Response buscarPorId(Long id) {
        log.info("[tournament-service] Buscando torneo con ID={}", id);
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[tournament-service] Torneo no localizado. ID={}", id);
                    return new TorneoNotFoundException(id);
                });
        return TorneoDTO.Response.fromEntity(torneo);
    }

    public TorneoDTO.Response actualizarTorneo(Long id, TorneoDTO.Request request) {
        log.info("[tournament-service] Actualizando datos del torneo ID={}", id);

        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new TorneoNotFoundException(id));

        if (!torneo.getNombre().equalsIgnoreCase(request.getNombre())
                && torneoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            log.warn("[tournament-service] Conflicto al renombrar: el nombre '{}' ya está en uso", request.getNombre());
            throw new TorneoYaExisteException(request.getNombre());
        }

        torneo.setNombre(request.getNombre());
        torneo.setGameId(request.getGameId());

        Torneo actualizado = torneoRepository.save(torneo);
        log.info("[tournament-service] Torneo ID={} guardado con nuevas modificaciones.", actualizado.getId());

        return TorneoDTO.Response.fromEntity(actualizado);
    }

    public void eliminarTorneo(Long id) {
        log.info("[tournament-service] Solicitud para dar de baja torneo ID={}", id);
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new TorneoNotFoundException(id));

        torneoRepository.delete(torneo);
        log.info("[tournament-service] Torneo ID={} eliminado permanentemente de la base de datos.", id);
    }
}