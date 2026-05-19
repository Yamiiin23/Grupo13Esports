package com.esports.rankingservice.service;

import com.esports.rankingservice.client.ResultServiceClient;
import com.esports.rankingservice.client.TournamentServiceClient;
import com.esports.rankingservice.dto.RankingDTO;
import com.esports.rankingservice.exception.RankingNotFoundException;
import com.esports.rankingservice.model.Ranking;
import com.esports.rankingservice.repository.RankingRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Transactional
public class RankingService {

    private static final Logger log = LoggerFactory.getLogger(RankingService.class);

    private final RankingRepository      rankingRepository;
    private final ResultServiceClient    resultClient;
    private final TournamentServiceClient tournamentClient;

    public RankingService(RankingRepository rankingRepository,
                          ResultServiceClient resultClient,
                          TournamentServiceClient tournamentClient) {
        this.rankingRepository  = rankingRepository;
        this.resultClient       = resultClient;
        this.tournamentClient   = tournamentClient;
    }


    public RankingDTO.Response registrarParticipante(RankingDTO.Request request) {
        log.info("[ranking-service] Registrando participante. torneoId={}, participanteId={}",
                request.getTorneoId(), request.getParticipanteId());

        validarTorneo(request.getTorneoId());

        if (rankingRepository.existsByTorneoIdAndParticipanteId(
                request.getTorneoId(), request.getParticipanteId())) {
            throw new IllegalStateException(
                "El participante ID=" + request.getParticipanteId()
                + " ya está registrado en el ranking del torneo ID=" + request.getTorneoId());
        }

        Ranking ranking = Ranking.builder()
                .torneoId(request.getTorneoId())
                .participanteId(request.getParticipanteId())
                .puntos(0).victorias(0).derrotas(0).diferencia(0).posicion(0)
                .build();

        Ranking guardado = rankingRepository.save(ranking);


        recalcularPosiciones(request.getTorneoId());

        log.info("[ranking-service] Participante registrado en ranking. ID={}", guardado.getId());
        return RankingDTO.Response.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public List<RankingDTO.Response> obtenerTabla(Long torneoId) {
        log.info("[ranking-service] Obteniendo tabla de posiciones. torneoId={}", torneoId);
        return rankingRepository.findByTorneoIdOrderByPosicion(torneoId)
                .stream().map(RankingDTO.Response::fromEntity).toList();
    }


    @Transactional(readOnly = true)
    public RankingDTO.Response buscarPorParticipante(Long torneoId, Long participanteId) {
        log.info("[ranking-service] Buscando posición. torneoId={}, participanteId={}", torneoId, participanteId);
        return RankingDTO.Response.fromEntity(
            rankingRepository.findByTorneoIdAndParticipanteId(torneoId, participanteId)
                .orElseThrow(() -> new RankingNotFoundException(torneoId, participanteId))
        );
    }

    @Transactional(readOnly = true)
    public RankingDTO.Response buscarPorId(Long id) {
        log.info("[ranking-service] Buscando ranking ID={}", id);
        return RankingDTO.Response.fromEntity(
            rankingRepository.findById(id)
                .orElseThrow(() -> new RankingNotFoundException(id))
        );
    }

    public List<RankingDTO.Response> actualizarConResultado(
            Long torneoId, RankingDTO.ActualizarRequest request) {

        log.info("[ranking-service] Actualizando ranking con resultado. torneoId={}, ganadorId={}, perdedorId={}",
                torneoId, request.getGanadorId(), request.getPerdedorId());
        Ranking rankingGanador = rankingRepository
                .findByTorneoIdAndParticipanteId(torneoId, request.getGanadorId())
                .orElseGet(() -> crearEntradaRanking(torneoId, request.getGanadorId()));

        Ranking rankingPerdedor = rankingRepository
                .findByTorneoIdAndParticipanteId(torneoId, request.getPerdedorId())
                .orElseGet(() -> crearEntradaRanking(torneoId, request.getPerdedorId()));

        rankingGanador.registrarVictoria(request.getPuntajeGanador(), request.getPuntajePerdedor());
        rankingPerdedor.registrarDerrota(request.getPuntajePerdedor(), request.getPuntajeGanador());

        rankingRepository.save(rankingGanador);
        rankingRepository.save(rankingPerdedor);

        recalcularPosiciones(torneoId);

        log.info("[ranking-service] Ranking actualizado y posiciones recalculadas. torneoId={}", torneoId);


        return List.of(
            RankingDTO.Response.fromEntity(rankingRepository.findById(rankingGanador.getId()).orElseThrow()),
            RankingDTO.Response.fromEntity(rankingRepository.findById(rankingPerdedor.getId()).orElseThrow())
        );
    }

    public void reiniciarRanking(Long torneoId) {
        log.info("[ranking-service] Reiniciando ranking del torneo ID={}", torneoId);

        List<Ranking> entradas = rankingRepository.findByTorneoIdOrderByPosicion(torneoId);
        entradas.forEach(r -> {
            r.setPuntos(0);
            r.setVictorias(0);
            r.setDerrotas(0);
            r.setDiferencia(0);
            r.setPosicion(0);
        });
        rankingRepository.saveAll(entradas);
        log.info("[ranking-service] Ranking reiniciado. Entradas={}", entradas.size());
    }

    private void recalcularPosiciones(Long torneoId) {
        List<Ranking> tabla = rankingRepository.findByTorneoIdOrderByPosicion(torneoId);
        AtomicInteger pos = new AtomicInteger(1);
        tabla.forEach(r -> r.setPosicion(pos.getAndIncrement()));
        rankingRepository.saveAll(tabla);
        log.info("[ranking-service] Posiciones recalculadas para torneoId={}. Total={}", torneoId, tabla.size());
    }

    private Ranking crearEntradaRanking(Long torneoId, Long participanteId) {
        log.info("[ranking-service] Creando entrada de ranking automática. torneoId={}, participanteId={}",
                torneoId, participanteId);
        Ranking nueva = Ranking.builder()
                .torneoId(torneoId)
                .participanteId(participanteId)
                .puntos(0).victorias(0).derrotas(0).diferencia(0).posicion(0)
                .build();
        return rankingRepository.save(nueva);
    }

    private void validarTorneo(Long torneoId) {
        try {
            tournamentClient.obtenerResumen(torneoId);
            log.info("[ranking-service] Torneo ID={} validado", torneoId);
        } catch (FeignException.NotFound e) {
            throw new IllegalStateException("Torneo ID=" + torneoId + " no encontrado");
        } catch (FeignException e) {
            log.error("[ranking-service] Error al contactar tournament-service: {}", e.getMessage());
            throw new IllegalStateException("No se pudo validar el torneo");
        }
    }
}
