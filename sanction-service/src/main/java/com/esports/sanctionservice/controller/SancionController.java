package com.esports.sanctionservice.controller;

import com.esports.sanctionservice.dto.SancionDTO;
import com.esports.sanctionservice.model.Sancion;
import com.esports.sanctionservice.service.SancionService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sanciones")
@Tag(name = "Sanciones", description = "Microservicio para la gestión, control, verificación y anulación lógica de sanciones a usuarios y equipos")
public class SancionController {

    private static final Logger log = LoggerFactory.getLogger(SancionController.class);
    private final SancionService sancionService;

    public SancionController(SancionService sancionService) {
        this.sancionService = sancionService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva sanción", description = "Registra una sanción en estado ACTIVA validando que el usuario o equipo existan en sus respectivos servicios")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sanción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o reglas de negocio rotas"),
            @ApiResponse(responseCode = "503", description = "Servicio externo no disponible")
    })
    public ResponseEntity<SancionDTO.Response> crearSancion(@Valid @RequestBody SancionDTO.Request request) {
        log.info("[sanction-service] POST /api/v1/sanciones");
        return ResponseEntity.status(HttpStatus.CREATED).body(sancionService.crearSancion(request));
    }

    @GetMapping
    @Operation(summary = "Listar y filtrar sanciones", description = "Retorna las sanciones registradas permitiendo filtrar por usuario, equipo o estado")
    public ResponseEntity<List<SancionDTO.Response>> listarSanciones(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long equipoId,
            @RequestParam(required = false) Sancion.EstadoSancion estado) {
        log.info("[sanction-service] GET /api/v1/sanciones");
        return ResponseEntity.ok(sancionService.listarSanciones(usuarioId, equipoId, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sanción por ID")
    public ResponseEntity<SancionDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[sanction-service] GET /api/v1/sanciones/{}", id);
        return ResponseEntity.ok(sancionService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sanción", description = "Modifica los datos de una sanción siempre y cuando no se encuentre CERRADA o ANULADA")
    public ResponseEntity<SancionDTO.Response> actualizarSancion(
            @PathVariable Long id, @Valid @RequestBody SancionDTO.Request request) {
        log.info("[sanction-service] PUT /api/v1/sanciones/{}", id);
        return ResponseEntity.ok(sancionService.actualizarSancion(id, request));
    }

    @Valid
    @PatchMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar formalmente una sanción", description = "Cambia el estado de la sanción a CERRADA")
    public ResponseEntity<SancionDTO.Response> cerrarSancion(
            @PathVariable Long id, @Valid @RequestBody SancionDTO.EstadoRequest request) {
        log.info("[sanction-service] PATCH /api/v1/sanciones/{}/cerrar", id);
        return ResponseEntity.ok(sancionService.cerrarSancion(id, request.getJustificacion()));
    }

    @GetMapping("/verificar/usuario/{usuarioId}")
    @Operation(summary = "Verificar bloqueo de un usuario", description = "Orquestación remota utilizada por otros servicios para saber si un usuario puede inscribirse o jugar")
    public ResponseEntity<SancionDTO.VerificacionResponse> verificarBloqueoUsuario(@PathVariable Long usuarioId) {
        log.info("[sanction-service] GET verificar bloqueo usuario ID={}", usuarioId);
        return ResponseEntity.ok(sancionService.verificarBloqueoUsuario(usuarioId));
    }

    @GetMapping("/verificar/equipo/{equipoId}")
    @Operation(summary = "Verificar bloqueo de un equipo")
    public ResponseEntity<SancionDTO.VerificacionResponse> verificarBloqueoEquipo(@PathVariable Long equipoId) {
        log.info("[sanction-service] GET verificar bloqueo equipo ID={}", equipoId);
        return ResponseEntity.ok(sancionService.verificarBloqueoEquipo(equipoId));
    }

    @PostMapping("/cerrar-vencidas")
    @Operation(summary = "Cierre automático de sanciones expiradas", description = "Busca en la base de datos aquellas sanciones cuya fecha de término ya pasó y las marca como CERRADAS")
    public ResponseEntity<Map<String, Object>> cerrarVencidas() {
        log.info("[sanction-service] POST cerrar-vencidas");
        int total = sancionService.cerrarSancionesVencidas();
        return ResponseEntity.ok(Map.of("sancionesCerradas", total));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Anulación lógica de una sanción", description = "Cambia el estado de una sanción a ANULADA para retirarla de los flujos activos sin destruirla físicamente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sanción anulada lógicamente de manera exitosa"),
            @ApiResponse(responseCode = "404", description = "Sanción no encontrada")
    })
    public ResponseEntity<SancionDTO.Response> anularSancionLogica(
            @PathVariable Long id, @RequestParam String justificacion) {
        log.info("[sanction-service] DELETE /api/v1/sanciones/{} (Anulación lógica)", id);
        return ResponseEntity.ok(sancionService.anularSancionLogica(id, justificacion));
    }
}