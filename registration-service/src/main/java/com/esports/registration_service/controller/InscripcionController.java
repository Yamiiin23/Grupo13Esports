package com.esports.registration_service.controller;

import com.esports.registration_service.dto.InscripcionDTO;
import com.esports.registration_service.service.InscripcionService;
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
@RequestMapping("/api/v1/inscripciones")
@Tag(name = "Inscripciones", description = "Microservicio de Admisión: Valida cupos y gestiona el ciclo de vida de ingreso de equipos a torneos")
public class InscripcionController {

    private static final Logger log = LoggerFactory.getLogger(InscripcionController.class);
    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping
    @Operation(summary = "Registrar un equipo en un torneo", description = "Genera una solicitud de inscripción en estado PENDIENTE")
    public ResponseEntity<InscripcionDTO.Response> registrarEquipo(@Valid @RequestBody InscripcionDTO.Request request) {
        log.info("[registration-service] POST /api/v1/inscripciones");
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcionService.registrarEquipo(request));
    }

    @GetMapping
    @Operation(summary = "Listar inscripciones", description = "Obtiene todas las inscripciones del sistema, opcionalmente filtradas por ID de torneo")
    public ResponseEntity<List<InscripcionDTO.Response>> listarInscripciones(@RequestParam(required = false) Long torneoId) {
        log.info("[registration-service] GET /api/v1/inscripciones");
        return ResponseEntity.ok(inscripcionService.listarInscripciones(torneoId));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Modificar estado de admisión", description = "Permite transicionar la solicitud entre PENDIENTE, ACEPTADA o RECHAZADA")
    public ResponseEntity<InscripcionDTO.Response> actualizarEstado(
            @PathVariable Long id, @Valid @RequestBody InscripcionDTO.UpdateStatusRequest request) {
        log.info("[registration-service] PUT /api/v1/inscripciones/{}/estado", id);
        return ResponseEntity.ok(inscripcionService.actualizarEstado(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelación lógica de una inscripción", description = "Inactiva la inscripción pasando su estado a CANCELADA para auditoría de eSports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción cancelada lógicamente con éxito"),
            @ApiResponse(responseCode = "400", description = "La justificación está vacía o la inscripción no se puede cancelar")
    })
    public ResponseEntity<InscripcionDTO.Response> cancelarInscripcion(
            @PathVariable Long id, @RequestParam String motivo) {
        log.info("[registration-service] DELETE /api/v1/inscripciones/{} (Baja Lógica)", id);
        return ResponseEntity.ok(inscripcionService.cancelarInscripcionLogica(id, motivo));
    }
}