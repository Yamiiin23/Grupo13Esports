package com.esports.resultservice.controller;

import com.esports.resultservice.dto.ResultadoDTO;
import com.esports.resultservice.model.Resultado;
import com.esports.resultservice.service.ResultadoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resultados")
public class ResultadoController {

    private static final Logger log = LoggerFactory.getLogger(ResultadoController.class);

    private final ResultadoService resultadoService;

    public ResultadoController(ResultadoService resultadoService) {
        this.resultadoService = resultadoService;
    }

    @PostMapping
    public ResponseEntity<ResultadoDTO.Response> crearResultado(
            @Valid @RequestBody ResultadoDTO.Request request) {
        log.info("[result-service] POST /api/v1/resultados");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resultadoService.crearResultado(request));
    }

    @GetMapping
    public ResponseEntity<List<ResultadoDTO.Response>> listarResultados(
            @RequestParam(required = false) Resultado.EstadoValidacion estado) {
        log.info("[result-service] GET /api/v1/resultados");
        return ResponseEntity.ok(resultadoService.listarResultados(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultadoDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[result-service] GET /api/v1/resultados/{}", id);
        return ResponseEntity.ok(resultadoService.buscarPorId(id));
    }

    @GetMapping("/partida/{partidaId}")
    public ResponseEntity<ResultadoDTO.Response> buscarPorPartida(@PathVariable Long partidaId) {
        log.info("[result-service] GET /api/v1/resultados/partida/{}", partidaId);
        return ResponseEntity.ok(resultadoService.buscarPorPartida(partidaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultadoDTO.Response> actualizarResultado(
            @PathVariable Long id,
            @Valid @RequestBody ResultadoDTO.Request request) {
        log.info("[result-service] PUT /api/v1/resultados/{}", id);
        return ResponseEntity.ok(resultadoService.actualizarResultado(id, request));
    }

    @PatchMapping("/{id}/validacion")
    public ResponseEntity<ResultadoDTO.Response> cambiarValidacion(
            @PathVariable Long id,
            @Valid @RequestBody ResultadoDTO.ValidacionRequest request) {
        log.info("[result-service] PATCH /api/v1/resultados/{}/validacion → {}", id, request.getEstadoValidacion());
        return ResponseEntity.ok(resultadoService.cambiarValidacion(id, request));
    }
}
