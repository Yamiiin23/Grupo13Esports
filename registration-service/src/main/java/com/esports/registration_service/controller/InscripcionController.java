package com.esports.registration_service.controller;

import com.esports.registration_service.dto.InscripcionDTO;
import com.esports.registration_service.service.InscripcionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inscripciones")
public class InscripcionController {

    private static final Logger log = LoggerFactory.getLogger(InscripcionController.class);
    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping
    public ResponseEntity<InscripcionDTO.Response> registrarEquipo(@Valid @RequestBody InscripcionDTO.Request request) {
        log.info("[registration-service] POST /api/v1/inscripciones");
        InscripcionDTO.Response response = inscripcionService.registrarEquipo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<InscripcionDTO.Response>> listarInscripciones(@RequestParam(required = false) Long torneoId) {
        log.info("[registration-service] GET /api/v1/inscripciones");
        List<InscripcionDTO.Response> response = inscripcionService.listarInscripciones(torneoId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<InscripcionDTO.Response> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody InscripcionDTO.UpdateStatusRequest request) {
        log.info("[registration-service] PUT /api/v1/inscripciones/{}/estado", id);
        InscripcionDTO.Response response = inscripcionService.actualizarEstado(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarInscripcion(@PathVariable Long id) {
        log.info("[registration-service] DELETE /api/v1/inscripciones/{}", id);
        inscripcionService.cancelarInscripcion(id);
        return ResponseEntity.noContent().build();
    }
}