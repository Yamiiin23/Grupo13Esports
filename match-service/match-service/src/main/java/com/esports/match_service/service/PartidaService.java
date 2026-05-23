package com.esports.match_service.service;

import com.esports.match_service.dto.PartidaDTO;
import com.esports.match_service.exception.PartidaNotFoundException;
import com.esports.match_service.exception.ValidacionPartidaException;
import com.esports.match_service.model.Partida;
import com.esports.match_service.repository.PartidaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PartidaService {

    private static final Logger log = LoggerFactory.getLogger(PartidaService.class);
    private final PartidaRepository partidaRepository;

    public PartidaService(PartidaRepository partidaRepository) {
        this.partidaRepository = partidaRepository;
    }

    public PartidaDTO.Response crearPartida(PartidaDTO.Request request) {
        log.info("[match-service] Creando emparejamiento para el Torneo ID={}", request.getTorneoId());

        if (request.getEquipoLocalId().equals(request.getEquipoVisitanteId())) {
            log.warn("[match-service] Conflicto de validación: Equipo Local y Visitante coinciden. ID={}", request.getEquipoLocalId());
            throw new ValidacionPartidaException("Un equipo no puede jugar contra sí mismo.");
        }

        Partida partida = Partida.builder()
                .torneoId(request.getTorneoId())
                .equipoLocalId(request.getEquipoLocalId())
                .equipoVisitanteId(request.getEquipoVisitanteId())
                .estado(Partida.EstadoPartida.PENDIENTE)
                .build();

        Partida guardada = partidaRepository.save(partida);
        log.info("[match-service] Partida agendada exitosamente con ID={}", guardada.getId());

        // Forzamos el uso explícito de PartidaDTO.Response para evitar que el IDE busque otra clase externa
        return PartidaDTO.Response.fromEntity(guardada);
    }

    @Transactional(readOnly = true)
    public List<PartidaDTO.Response> listarPartidas(Long torneoId) {
        log.info("[match-service] Listando partidas del sistema con filtro de torneo={}", torneoId);

        List<Partida> partidas = (torneoId != null)
                ? partidaRepository.findByTorneoId(torneoId)
                : partidaRepository.findAll();

        return partidas.stream()
                .map(PartidaDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PartidaDTO.Response buscarPorId(Long id) {
        log.info("[match-service] Solicitando detalles de la partida ID={}", id);
        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new PartidaNotFoundException(id));
        return PartidaDTO.Response.fromEntity(partida);
    }

    public PartidaDTO.Response actualizarResultado(Long id, PartidaDTO.UpdateResultRequest request) {
        log.info("[match-service] Actualizando marcador de la partida ID={}", id);

        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new PartidaNotFoundException(id));

        try {
            Partida.EstadoPartida nuevoEstado = Partida.EstadoPartida.valueOf(request.getEstado().toUpperCase());
            partida.setEstado(nuevoEstado);
        } catch (IllegalArgumentException e) {
            log.error("[match-service] Intento erróneo de asignación de estado: {}", request.getEstado());
            throw new ValidacionPartidaException("Estado inválido. Los valores válidos son: PENDIENTE, EN_CURSO, FINALIZADA");
        }

        partida.setResultado(request.getResultado());
        Partida actualizada = partidaRepository.save(partida);
        log.info("[match-service] Marcador actualizado con éxito para la partida ID={}", actualizada.getId());

        return PartidaDTO.Response.fromEntity(actualizada);
    }

    public void eliminarPartida(Long id) {
        log.info("[match-service] Cancelando y eliminando partida ID={}", id);
        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new PartidaNotFoundException(id));
        partidaRepository.delete(partida);
        log.info("[match-service] Partida ID={} borrada del fixture.", id);
    }
}