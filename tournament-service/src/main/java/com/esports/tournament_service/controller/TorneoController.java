package com.esports.tournament_service.controller;

import com.esports.tournament_service.dto.TorneoDTO;
import com.esports.tournament_service.service.TorneoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/torneos")
public class TorneoController {

    private static final Logger log = LoggerFactory.getLogger(TorneoController.class);
    private final TorneoService torneoService;

    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    @PostMapping
    public ResponseEntity<TorneoDTO.Response> crearTorneo(@Valid @RequestBody TorneoDTO.Request request) {
        log.info("[tournament-service] POST /api/v1/torneos");
        TorneoDTO.Response response = torneoService.crearTorneo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TorneoDTO.Response>> listarTorneos() {
        log.info("[tournament-service] GET /api/v1/torneos");
        List<TorneoDTO.Response> response = torneoService.listarTorneos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TorneoDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[tournament-service] GET /api/v1/torneos/{}", id);
        TorneoDTO.Response response = torneoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TorneoDTO.Response> actualizarTorneo(
            @PathVariable Long id,
            @Valid @RequestBody TorneoDTO.Request request) {
        log.info("[tournament-service] PUT /api/v1/torneos/{}", id);
        TorneoDTO.Response response = torneoService.actualizarTorneo(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTorneo(@PathVariable Long id) {
        log.info("[tournament-service] DELETE /api/v1/torneos/{}", id);
        torneoService.eliminarTorneo(id);
        return ResponseEntity.noContent().build();
    }
}