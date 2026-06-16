package com.esports.tournament_service.controller;

import com.esports.tournament_service.dto.TorneoDTO;
import com.esports.tournament_service.service.TorneoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/torneos")
@Tag(name = "Torneos", description = "Microservicio para la gestión de campeonatos, control de estados de juego y orquestación logística")
public class TorneoController {

    private static final Logger log = LoggerFactory.getLogger(TorneoController.class);
    private final TorneoService torneoService;

    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo torneo", description = "Registra un torneo en estado inicial 'INSCRIPCION'")
    public ResponseEntity<TorneoDTO.Response> crearTorneo(@Valid @RequestBody TorneoDTO.Request request) {
        log.info("[tournament-service] POST /api/v1/torneos");
        return ResponseEntity.status(HttpStatus.CREATED).body(torneoService.crearTorneo(request));
    }

    @GetMapping
    @Operation(summary = "Listar torneos activos", description = "Retorna el listado completo de torneos sin incluir los anulados")
    public ResponseEntity<List<TorneoDTO.Response>> listarTorneos() {
        log.info("[tournament-service] GET /api/v1/torneos");
        return ResponseEntity.ok(torneoService.listarTorneos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar torneo por ID")
    public ResponseEntity<TorneoDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[tournament-service] GET /api/v1/torneos/{}", id);
        return ResponseEntity.ok(torneoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar torneo", description = "Permite modificar los parámetros del torneo si está en fase de inscripción")
    public ResponseEntity<TorneoDTO.Response> actualizarTorneo(
            @PathVariable Long id, @Valid @RequestBody TorneoDTO.Request request) {
        log.info("[tournament-service] PUT /api/v1/torneos/{}", id);
        return ResponseEntity.ok(torneoService.actualizarTorneo(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Anulación lógica de un torneo", description = "Invalida el torneo y cancela sus flujos cambiando su estado a ANULADO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo anulado lógicamente con éxito"),
            @ApiResponse(responseCode = "400", description = "La justificación está vacía o el estado actual impide la operación")
    })
    public ResponseEntity<TorneoDTO.Response> eliminarTorneo(
            @PathVariable Long id, @RequestParam String justificacion) {
        log.info("[tournament-service] DELETE /api/v1/torneos/{} (Anulación Lógica)", id);
        return ResponseEntity.ok(torneoService.eliminarTorneoLogico(id, justificacion));
    }

    @PatchMapping("/{id}/iniciar")
    @Operation(summary = "Cambiar el estado del torneo a EN_CURSO", description = "Ejecuta algoritmos de validación competitiva (mínimo de participantes) antes de dar el vamos")
    public ResponseEntity<TorneoDTO.Response> iniciarTorneo(@PathVariable Long id) {
        log.info("[tournament-service] PATCH /api/v1/torneos/{}/iniciar", id);
        return ResponseEntity.ok(torneoService.iniciarTorneo(id));
    }
}