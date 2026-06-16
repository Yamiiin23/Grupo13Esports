package com.esports.ranking_service.controller;

import com.esports.ranking_service.dto.RankingDTO;
import com.esports.ranking_service.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rankings")
@Tag(name = "Rankings", description = "Microservicio para el cálculo matemático, actualización de posiciones y descalificación lógica de participantes")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registrar participante", description = "Inscribe a un nuevo participante en la tabla de posiciones inicial de un torneo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Participante registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Regla de negocio rota o torneo inválido")
    })
    public ResponseEntity<RankingDTO.Response> registrarParticipante(@Valid @RequestBody RankingDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rankingService.registrarParticipante(request));
    }

    @GetMapping("/torneo/{torneoId}")
    @Operation(summary = "Obtener tabla de posiciones", description = "Retorna la tabla clasificatoria ordenada algorítmicamente por puntos y diferencia de rondas")
    public ResponseEntity<List<RankingDTO.Response>> obtenerTabla(@PathVariable Long torneoId) {
        return ResponseEntity.ok(rankingService.obtenerTabla(torneoId));
    }

    @PutMapping("/torneo/{torneoId}/resultado")
    @Operation(summary = "Actualizar resultado y recalcular", description = "Registra los datos de un enfrentamiento directo y recalcula las posiciones del torneo de forma automática")
    public ResponseEntity<List<RankingDTO.Response>> actualizarResultado(
            @PathVariable Long torneoId,
            @Valid @RequestBody RankingDTO.ActualizarRequest request) {
        return ResponseEntity.ok(rankingService.actualizarConResultado(torneoId, request));
    }

    @DeleteMapping("/torneo/{torneoId}/participante/{participanteId}")
    @Operation(summary = "Anulación lógica de un participante", description = "Cambia el estado del participante a ANULADO retirándolo de la tabla y recalculando las posiciones de los demás")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participante anulado lógicamente y posiciones reestructuradas"),
            @ApiResponse(responseCode = "404", description = "Registro de ranking no encontrado")
    })
    public ResponseEntity<List<RankingDTO.Response>> eliminarParticipante(
            @PathVariable Long torneoId,
            @PathVariable Long participanteId,
            @RequestParam String justificacion) {
        return ResponseEntity.ok(rankingService.eliminarParticipanteLogico(torneoId, participanteId, justificacion));
    }
}