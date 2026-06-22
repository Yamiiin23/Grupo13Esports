package com.esports.ranking_service.service;

import com.esports.ranking_service.client.TournamentClient;
import com.esports.ranking_service.dto.RankingDTO;
import com.esports.ranking_service.exception.RankingNotFoundException;
import com.esports.ranking_service.exception.RankingValidationException;
import com.esports.ranking_service.model.Ranking;
import com.esports.ranking_service.repository.RankingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class RankingService {

    private static final Logger log = LoggerFactory.getLogger(RankingService.class);

    private final RankingRepository rankingRepository;
    private final TournamentClient tournamentClient;

    public RankingService(RankingRepository rankingRepository, TournamentClient tournamentClient) {
        this.rankingRepository = rankingRepository;
        this.tournamentClient = tournamentClient;
    }

    public RankingDTO.Response registrarParticipante(RankingDTO.Request request) {
        log.info("[ranking-service] Inscribiendo participante {} en torneo {}", request.getParticipanteId(), request.getTorneoId());

        try {
            tournamentClient.obtenerTorneoPorId(request.getTorneoId());
        } catch (Exception e) {
            throw new RankingValidationException("El torneo especificado con ID " + request.getTorneoId() + " no existe en el sistema.");
        }

        if (rankingRepository.existsByTorneoIdAndParticipanteId(request.getTorneoId(), request.getParticipanteId())) {
            throw new RankingValidationException("El participante ya se encuentra registrado en la tabla de este torneo.");
        }

        Ranking nuevo = Ranking.builder()
                .torneoId(request.getTorneoId())
                .participanteId(request.getParticipanteId())
                .estado(Ranking.EstadoRanking.ACTIVO)
                .build();

        Ranking guardado = rankingRepository.save(nuevo);
        recalcularPosiciones(request.getTorneoId());

        return RankingDTO.Response.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public List<RankingDTO.Response> obtenerTabla(Long torneoId) {
        return rankingRepository.findByTorneoIdOrderByPosicion(torneoId).stream()
                .filter(r -> r.getEstado() == Ranking.EstadoRanking.ACTIVO)
                .map(RankingDTO.Response::fromEntity)
                .toList();
    }

    public List<RankingDTO.Response> actualizarConResultado(Long torneoId, RankingDTO.ActualizarRequest request) {
        Ranking ganador = rankingRepository.findByTorneoIdAndParticipanteId(torneoId, request.getGanadorId())
                .orElseThrow(() -> new RankingNotFoundException("Ganador no registrado en este torneo"));

        Ranking perdedor = rankingRepository.findByTorneoIdAndParticipanteId(torneoId, request.getPerdedorId())
                .orElseThrow(() -> new RankingNotFoundException("Perdedor no registrado en este torneo"));

        if (ganador.getEstado() == Ranking.EstadoRanking.ANULADO || perdedor.getEstado() == Ranking.EstadoRanking.ANULADO) {
            throw new RankingValidationException("No se pueden registrar resultados para participantes anulados o descalificados.");
        }

        int difRondas = Math.abs(request.getPuntajeGanador() - request.getPuntajePerdedor());

        ganador.registrarVictoria(3, difRondas);
        perdedor.registrarDerrota(difRondas);

        rankingRepository.save(ganador);
        rankingRepository.save(perdedor);

        return recalcularPosiciones(torneoId);
    }

    public List<RankingDTO.Response> eliminarParticipanteLogico(Long torneoId, Long participanteId, String justificacion) {
        if (justificacion == null || justificacion.isBlank()) {
            throw new RankingValidationException("La justificación de la baja es obligatoria.");
        }

        Ranking ranking = rankingRepository.findByTorneoIdAndParticipanteId(torneoId, participanteId)
                .orElseThrow(() -> new RankingNotFoundException("El participante no pertenece a este torneo"));

        if (ranking.getEstado() == Ranking.EstadoRanking.ANULADO) {
            throw new RankingValidationException("El competidor ya fue dado de baja previamente.");
        }

        ranking.setEstado(Ranking.EstadoRanking.ANULADO);
        ranking.setMotivoBaja(justificacion);
        ranking.setPosicion(0);

        rankingRepository.save(ranking);
        log.info("[ranking-service] Participante ID={} anulado lógicamente del torneo ID={}", participanteId, torneoId);

        return recalcularPosiciones(torneoId);
    }

    private List<RankingDTO.Response> recalcularPosiciones(Long torneoId) {
        List<Ranking> todosLosRegistros = rankingRepository.findByTorneoIdOrderByPosicion(torneoId);

        List<Ranking> activos = new ArrayList<>(todosLosRegistros.stream()
                .filter(r -> r.getEstado() == Ranking.EstadoRanking.ACTIVO)
                .toList());

        activos.sort(Comparator.comparingInt(Ranking::getPuntos).reversed()
                .thenComparingInt(Ranking::getDiferencia).reversed());

        for (int i = 0; i < activos.size(); i++) {
            activos.get(i).setPosicion(i + 1);
        }

        rankingRepository.saveAll(activos);

        return activos.stream()
                .map(RankingDTO.Response::fromEntity)
                .toList();
    }
}