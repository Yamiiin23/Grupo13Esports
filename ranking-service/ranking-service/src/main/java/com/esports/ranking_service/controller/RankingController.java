package com.esports.ranking_service.controller;

import com.esports.ranking_service.dto.RankingDTO;
import com.esports.ranking_service.service.RankingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rankings")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @PostMapping
    public ResponseEntity<RankingDTO.Response> registrar(@Valid @RequestBody RankingDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rankingService.registrarParticipante(request));
    }

    @GetMapping ("/torneo/{torneoId}")
    public ResponseEntity<List<RankingDTO.Response>> obtenerTabla(@PathVariable Long torneoId) {
        return ResponseEntity.ok(rankingService.obtenerTabla(torneoId));
    }

    @PostMapping("/torneo/{torneoId}/actualizar")
    public ResponseEntity<List<RankingDTO.Response>> actualizar(@PathVariable Long torneoId, @Valid @RequestBody RankingDTO.ActualizarRequest request) {
        return ResponseEntity.ok(rankingService.actualizarConResultado(torneoId, request));
    }
}