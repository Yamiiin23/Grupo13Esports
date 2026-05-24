package com.esports.ranking_service.service;

import com.esports.ranking_service.client.TournamentServiceClient;
import com.esports.ranking_service.dto.RankingDTO;
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
    private final TournamentServiceClient tournamentClient;

    public RankingService(RankingRepository rankingRepository, TournamentServiceClient tournamentClient) {
        this.rankingRepository = rankingRepository;
        this.tournamentClient = tournamentClient;
    }

    public RankingDTO.Response registrarParticipante(RankingDTO.Request request) {
        log.info("[ranking-service] Inscribiendo participante {} en torneo {}", request.getParticipanteId(), request.getTorneoId());

        try {
            tournamentClient.obtenerTorneoPorId(request.getTorneoId());
        } catch (Exception e) {
            throw new IllegalStateException("El torneo especificado no existe.");
        }

        if (rankingRepository.existsByTorneoIdAndParticipanteId(request.getTorneoId(), request.getParticipanteId())) {
            throw new IllegalStateException("El participante ya está en el torneo.");
        }

        Ranking nuevo = Ranking.builder()
                .torneoId(request.getTorneoId())
                .participanteId(request.getParticipanteId())
                .build();

        Ranking guardado = rankingRepository.save(nuevo);
        recalcularPosiciones(request.getTorneoId());

        return RankingDTO.Response.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public List<RankingDTO.Response> obtenerTabla(Long torneoId) {
        return rankingRepository.findByTorneoIdOrderByPosicion(torneoId).stream()
                .map(RankingDTO.Response::fromEntity)
                .toList();
    }

    public List<RankingDTO.Response> actualizarConResultado(Long torneoId, RankingDTO.ActualizarRequest request) {
        Ranking ganador = rankingRepository.findByTorneoIdAndParticipanteId(torneoId, request.getGanadorId())
                .orElseThrow(() -> new IllegalArgumentException("Ganador no registrado en el torneo"));

        Ranking perdedor = rankingRepository.findByTorneoIdAndParticipanteId(torneoId, request.getPerdedorId())
                .orElseThrow(() -> new IllegalArgumentException("Perdedor no registrado en el torneo"));

        int difRondas = Math.abs(request.getPuntajeGanador() - request.getPuntajePerdedor());

        ganador.registrarVictoria(3, difRondas);
        perdedor.registrarDerrota(difRondas);

        rankingRepository.save(ganador);
        rankingRepository.save(perdedor);

        return recalcularPosiciones(torneoId);
    }
    private List<RankingDTO.Response> recalcularPosiciones(Long torneoId) {
        List<Ranking> listaRepositorio = rankingRepository.findByTorneoIdOrderByPosicion(torneoId);

        List<Ranking> lista = new ArrayList<>(listaRepositorio);

        lista.sort(Comparator.comparingInt(Ranking::getPuntos).reversed()
                .thenComparingInt(Ranking::getDiferencia).reversed());

        for (int i = 0; i < lista.size(); i++) {
            lista.get(i).setPosicion(i + 1);
        }
        rankingRepository.saveAll(lista);
        return lista.stream()
                .map(RankingDTO.Response::fromEntity)
                .toList();
    }
}
