package com.esports.teamservice.controller;

import com.esports.teamservice.dto.EquipoDTO;
import com.esports.teamservice.model.Equipo;
import com.esports.teamservice.service.EquipoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/equipos")
@Tag(name = "Equipos", description = "Endpoints para la gestion de equipos y miembros")
public class EquipoController {

    private static final Logger log = LoggerFactory.getLogger(EquipoController.class);
    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @PostMapping
    @Operation(summary = "Crear un equipo", description = "Crea un equipo validando externamente al capitan y al juego principal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipo creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "422", description = "Error en las validaciones de negocio")
    })
    public ResponseEntity<EquipoDTO.Response> crearEquipo(@Valid @RequestBody EquipoDTO.Request request) {
        log.info("REST request para crear equipo: {}", request.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(equipoService.crearEquipo(request));
    }

    @GetMapping
    @Operation(summary = "Listar equipos", description = "Retorna los equipos registrados permitiendo filtrar opcionalmente por juego y estado")
    public ResponseEntity<List<EquipoDTO.Response>> listarEquipos(
            @RequestParam(required = false) Long juegoId,
            @RequestParam(required = false) Equipo.EstadoEquipo estado) {
        log.info("REST request para listar equipos con filtros");
        return ResponseEntity.ok(equipoService.listarEquipos(juegoId, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener equipo por ID")
    public ResponseEntity<EquipoDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("REST request para obtener equipo: {}", id);
        return ResponseEntity.ok(equipoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un equipo")
    public ResponseEntity<EquipoDTO.Response> actualizarEquipo(
            @PathVariable Long id,
            @Valid @RequestBody EquipoDTO.Request request) {
        log.info("REST request para actualizar equipo ID: {}", id);
        return ResponseEntity.ok(equipoService.actualizarEquipo(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un equipo (Baja logica)")
    public ResponseEntity<EquipoDTO.Response> desactivarEquipo(@PathVariable Long id) {
        log.info("REST request para desactivar equipo ID: {}", id);
        return ResponseEntity.ok(equipoService.desactivarEquipo(id));
    }

    @PostMapping("/{id}/miembros")
    @Operation(summary = "Agregar un miembro al equipo")
    public ResponseEntity<EquipoDTO.Response> agregarMiembro(
            @PathVariable Long id,
            @Valid @RequestBody EquipoDTO.MiembroRequest request) {
        log.info("REST request para agregar miembro al equipo ID: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(equipoService.agregarMiembro(id, request));
    }

    @DeleteMapping("/{id}/miembros/{usuarioId}")
    @Operation(summary = "Eliminar un miembro del equipo")
    public ResponseEntity<EquipoDTO.Response> eliminarMiembro(@PathVariable Long id, @PathVariable Long usuarioId) {
        log.info("REST request para eliminar miembro {} del equipo {}", usuarioId, id);
        return ResponseEntity.ok(equipoService.eliminarMiembro(id, usuarioId));
    }
}