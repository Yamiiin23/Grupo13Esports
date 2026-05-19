package com.esports.rankingservice.controller;

import com.esports.rankingservice.dto.RankingDTO;
import com.esports.rankingservice.service.RankingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/rankings")
public class RankingController {

    private static final Logger log = LoggerFactory.getLogger(RankingController.class);

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @PostMapping
    public ResponseEntity<RankingDTO.Response> registrarParticipante(
            @Valid @RequestBody RankingDTO.Request request) {
        log.info("[ranking-service] POST /api/v1/rankings");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rankingService.registrarParticipante(request));
    }

    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<RankingDTO.Response>> obtenerTabla(@PathVariable Long torneoId) {
        log.info("[ranking-service] GET /api/v1/rankings/torneo/{}", torneoId);
        return ResponseEntity.ok(rankingService.obtenerTabla(torneoId));
    }


    @GetMapping("/torneo/{torneoId}/participante/{participanteId}")
    public ResponseEntity<RankingDTO.Response> buscarPorParticipante(
            @PathVariable Long torneoId,
            @PathVariable Long participanteId) {
        log.info("[ranking-service] GET /api/v1/rankings/torneo/{}/participante/{}", torneoId, participanteId);
        return ResponseEntity.ok(rankingService.buscarPorParticipante(torneoId, participanteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RankingDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[ranking-service] GET /api/v1/rankings/{}", id);
        return ResponseEntity.ok(rankingService.buscarPorId(id));
    }

    @PostMapping("/torneo/{torneoId}/actualizar")
    public ResponseEntity<List<RankingDTO.Response>> actualizarConResultado(
            @PathVariable Long torneoId,
            @Valid @RequestBody RankingDTO.ActualizarRequest request) {
        log.info("[ranking-service] POST /api/v1/rankings/torneo/{}/actualizar", torneoId);
        return ResponseEntity.ok(rankingService.actualizarConResultado(torneoId, request));
    }

    @DeleteMapping("/torneo/{torneoId}/reiniciar")
    public ResponseEntity<Map<String, String>> reiniciarRanking(@PathVariable Long torneoId) {
        log.info("[ranking-service] DELETE /api/v1/rankings/torneo/{}/reiniciar", torneoId);
        rankingService.reiniciarRanking(torneoId);
        return ResponseEntity.ok(Map.of("mensaje", "Ranking reiniciado correctamente para torneo ID=" + torneoId));
    }
}
