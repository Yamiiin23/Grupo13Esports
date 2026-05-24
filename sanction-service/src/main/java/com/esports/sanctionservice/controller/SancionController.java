package com.esports.sanctionservice.controller;

import com.esports.sanctionservice.dto.SancionDTO;
import com.esports.sanctionservice.model.Sancion;
import com.esports.sanctionservice.service.SancionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/sanciones")
public class SancionController {

    private static final Logger log = LoggerFactory.getLogger(SancionController.class);

    private final SancionService sancionService;

    public SancionController(SancionService sancionService) {
        this.sancionService = sancionService;
    }

    @PostMapping
    public ResponseEntity<SancionDTO.Response> crearSancion(
            @Valid @RequestBody SancionDTO.Request request) {
        log.info("[sanction-service] POST /api/v1/sanciones");
        return ResponseEntity.status(HttpStatus.CREATED).body(sancionService.crearSancion(request));
    }

    @GetMapping
    public ResponseEntity<List<SancionDTO.Response>> listarSanciones(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long equipoId,
            @RequestParam(required = false) Sancion.EstadoSancion estado) {
        log.info("[sanction-service] GET /api/v1/sanciones");
        return ResponseEntity.ok(sancionService.listarSanciones(usuarioId, equipoId, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SancionDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[sanction-service] GET /api/v1/sanciones/{}", id);
        return ResponseEntity.ok(sancionService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SancionDTO.Response> actualizarSancion(
            @PathVariable Long id,
            @Valid @RequestBody SancionDTO.Request request) {
        log.info("[sanction-service] PUT /api/v1/sanciones/{}", id);
        return ResponseEntity.ok(sancionService.actualizarSancion(id, request));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<SancionDTO.Response> cerrarSancion(
            @PathVariable Long id,
            @Valid @RequestBody SancionDTO.EstadoRequest request) {
        log.info("[sanction-service] PATCH /api/v1/sanciones/{}/cerrar", id);
        return ResponseEntity.ok(sancionService.cerrarSancion(id, request.getJustificacion()));
    }

    @GetMapping("/verificar/usuario/{usuarioId}")
    public ResponseEntity<SancionDTO.VerificacionResponse> verificarBloqueoUsuario(
            @PathVariable Long usuarioId) {
        log.info("[sanction-service] GET verificar bloqueo usuario ID={}", usuarioId);
        return ResponseEntity.ok(sancionService.verificarBloqueoUsuario(usuarioId));
    }

    @GetMapping("/verificar/equipo/{equipoId}")
    public ResponseEntity<SancionDTO.VerificacionResponse> verificarBloqueoEquipo(
            @PathVariable Long equipoId) {
        log.info("[sanction-service] GET verificar bloqueo equipo ID={}", equipoId);
        return ResponseEntity.ok(sancionService.verificarBloqueoEquipo(equipoId));
    }


    @PostMapping("/cerrar-vencidas")
    public ResponseEntity<Map<String, Object>> cerrarVencidas() {
        log.info("[sanction-service] POST cerrar-vencidas");
        int total = sancionService.cerrarSancionesVencidas();
        return ResponseEntity.ok(Map.of("sancionesCerradas", total));
    }
}
