package com.esports.ranking_service.service;

import com.esports.ranking_service.client.TournamentServiceClient;
import com.esports.ranking_service.dto.RankingDTO;
import com.esports.ranking_service.exception.RankingNotFoundException;
import com.esports.ranking_service.model.Ranking;
import com.esports.ranking_service.repository.RankingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class RankingService {

    private final RankingRepository rankingRepository;
    private final TournamentServiceClient tournamentClient;

    public RankingService(RankingRepository rankingRepository, TournamentServiceClient tournamentClient) {
        this.rankingRepository = rankingRepository;
        this.tournamentClient = tournamentClient;
    }

    public RankingDTO.Response registrarParticipante(RankingDTO.Request request) {
        try {
            tournamentClient.obtenerResumen(request.getTorneoId());
        } catch (Exception e) {
            throw new IllegalStateException("El torneo especificado no existe.");
        }

        if (rankingRepository.existsByTorneoIdAndParticipanteId(request.getTorneoId(), request.getParticipanteId())) {
            throw new IllegalStateException("El participante ya está en el torneo.");
        }

        Ranking ranking = Ranking.builder()
                .torneoId(request.getTorneoId())
                .participanteId(request.getParticipanteId())
                .build();

        Ranking guardado = rankingRepository.save(ranking);
        recalcularPosiciones(request.getTorneoId());
        return RankingDTO.Response.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public List<RankingDTO.Response> obtenerTabla(Long torneoId) {
        return rankingRepository.findByTorneoIdOrderByPosicion(torneoId).stream()
                .map(RankingDTO.Response::fromEntity).toList();
    }

    public List<RankingDTO.Response> actualizarConResultado(Long torneoId, RankingDTO.ActualizarRequest request) {
        Ranking ganador = rankingRepository.findByTorneoIdAndParticipanteId(torneoId, request.getGanadorId())
                .orElseGet(() -> rankingRepository.save(Ranking.builder().torneoId(torneoId).participanteId(request.getGanadorId()).build()));

        Ranking perdedor = rankingRepository.findByTorneoIdAndParticipanteId(torneoId, request.getPerdedorId())
                .orElseGet(() -> rankingRepository.save(Ranking.builder().torneoId(torneoId).participanteId(request.getPerdedorId()).build()));

        ganador.registrarVictoria(request.getPuntajeGanador(), request.getPuntajePerdedor());
        perdedor.registrarDerrota(request.getPuntajePerdedor(), request.getPuntajeGanador());

        rankingRepository.save(ganador);
        rankingRepository.save(perdedor);
        recalcularPosiciones(torneoId);

        return List.of(RankingDTO.Response.fromEntity(ganador), RankingDTO.Response.fromEntity(perdedor));
    }

    private void recalcularPosiciones(Long torneoId) {
        List<Ranking> tabla = rankingRepository.findByTorneoIdOrderByPosicion(torneoId);
        AtomicInteger pos = new AtomicInteger(1);
        tabla.forEach(r -> r.setPosicion(pos.getAndIncrement()));
        rankingRepository.saveAll(tabla);
    }
}