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
@Tag(name = "Rankings", description = "Operaciones relacionadas con los rankings")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registrar participante", description = "Registrar un nuevo participante en el sistema de rankings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Participante registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida")
    })
    public ResponseEntity<RankingDTO.Response> registrarParticipante(@Valid @RequestBody RankingDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rankingService.registrarParticipante(request));
    }

    @GetMapping("/torneo/{torneoId}")
    @Operation(summary = "Obtener tabla de posiciones", description = "Obtiene tabla de clasificaciones de un torneo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tabla obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Torneo no encontrado")
    })
    public ResponseEntity<List<RankingDTO.Response>> obtenerTabla(@PathVariable Long torneoId) {
        return ResponseEntity.ok(rankingService.obtenerTabla(torneoId));
    }

    @PutMapping("/torneo/{torneoId}/resultado")
    @Operation(summary = "Actualizar resultado", description = "Actualiza los resultado de un participante en un torneo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "404", description = "Torneo o participante no encontrado")
    })
    public ResponseEntity<List<RankingDTO.Response>> actualizarResultado(
            @PathVariable Long torneoId,
            @Valid @RequestBody RankingDTO.ActualizarRequest request) {
        return ResponseEntity.ok(rankingService.actualizarConResultado(torneoId, request));
    }
}