package com.esports.teamservice.controller;

import com.esports.teamservice.dto.EquipoDTO;
import com.esports.teamservice.model.Equipo;
import com.esports.teamservice.service.EquipoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipos")
public class EquipoController {

    private static final Logger log = LoggerFactory.getLogger(EquipoController.class);
    private final EquipoService equipoService;
    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @PostMapping
    public ResponseEntity<EquipoDTO.Response> crearEquipo(
            @Valid @RequestBody EquipoDTO.Request request) {

        log.info("[team-service] POST /api/v1/equipos - nombre={}", request.getNombre());
        EquipoDTO.Response response = equipoService.crearEquipo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EquipoDTO.Response>> listarEquipos(
            @RequestParam(required = false) Long juegoId,
            @RequestParam(required = false) Equipo.EstadoEquipo estado) {

        log.info("[team-service] GET /api/v1/equipos - juegoId={}, estado={}", juegoId, estado);
        return ResponseEntity.ok(equipoService.listarEquipos(juegoId, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[team-service] GET /api/v1/equipos/{}", id);
        return ResponseEntity.ok(equipoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoDTO.Response> actualizarEquipo(
            @PathVariable Long id,
            @Valid @RequestBody EquipoDTO.Request request) {

        log.info("[team-service] PUT /api/v1/equipos/{}", id);
        return ResponseEntity.ok(equipoService.actualizarEquipo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EquipoDTO.Response> desactivarEquipo(@PathVariable Long id) {
        log.info("[team-service] DELETE /api/v1/equipos/{}", id);
        return ResponseEntity.ok(equipoService.desactivarEquipo(id));
    }

    @PostMapping("/{id}/miembros")
    public ResponseEntity<EquipoDTO.Response> agregarMiembro(
            @PathVariable Long id,
            @Valid @RequestBody EquipoDTO.MiembroRequest request) {

        log.info("[team-service] POST /api/v1/equipos/{}/miembros - usuarioId={}", id, request.getUsuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(equipoService.agregarMiembro(id, request));
    }

    @DeleteMapping("/{id}/miembros/{usuarioId}")
    public ResponseEntity<EquipoDTO.Response> eliminarMiembro(
            @PathVariable Long id,
            @PathVariable Long usuarioId) {

        log.info("[team-service] DELETE /api/v1/equipos/{}/miembros/{}", id, usuarioId);
        return ResponseEntity.ok(equipoService.eliminarMiembro(id, usuarioId));
    }
}
