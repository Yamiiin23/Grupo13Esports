package com.esports.match_service.controller;

import com.esports.match_service.dto.PartidaDTO;
import com.esports.match_service.service.PartidaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/partidas")
public class PartidaController {

    private static final Logger log = LoggerFactory.getLogger(PartidaController.class);
    private final PartidaService partidaService;

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @PostMapping
    public ResponseEntity<PartidaDTO.Response> crearPartida(
            @Valid @RequestBody PartidaDTO.Request request) {
        log.info("[match-service] POST /api/v1/partidas - registrando nuevo enfrentamiento");
        PartidaDTO.Response response = partidaService.crearPartida(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PartidaDTO.Response>> listarPartidas(
            @RequestParam(required = false) Long torneoId) {
        log.info("[match-service] GET /api/v1/partidas - filtro torneoId={}", torneoId);
        List<PartidaDTO.Response> response = partidaService.listarPartidas(torneoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidaDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[match-service] GET /api/v1/partidas/{}", id);
        PartidaDTO.Response response = partidaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/resultado")
    public ResponseEntity<PartidaDTO.Response> actualizarResultado(
            @PathVariable Long id,
            @Valid @RequestBody PartidaDTO.UpdateResultRequest request) {
        log.info("[match-service] PUT /api/v1/partidas/{}/resultado", id);
        PartidaDTO.Response response = partidaService.actualizarResultado(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPartida(@PathVariable Long id) {
        log.info("[match-service] DELETE /api/v1/partidas/{}", id);
        partidaService.eliminarPartida(id);
        return ResponseEntity.noContent().build();
    }
}