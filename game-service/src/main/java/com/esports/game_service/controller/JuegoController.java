package com.esports.game_service.controller;

import com.esports.game_service.dto.JuegoDTO;
import com.esports.game_service.service.JuegoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/v1/juegos")
@Tag(name = "Módulo Juegos", description = "Controlador interactivo para la administración integral del catálogo de videojuegos competitivos de eSports")
public class JuegoController {

    private static final Logger log = LoggerFactory.getLogger(JuegoController.class);
    private final JuegoService juegoService;

    public JuegoController(JuegoService juegoService) {
        this.juegoService = juegoService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo juego", description = "Registra un nuevo videojuego competitivo en el catálogo general.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Juego creado exitosamente",
                    content = @Content(schema = @Schema(implementation = JuegoDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud de creación inválida o parámetros insuficientes")
    })
    public ResponseEntity<JuegoDTO.Response> crearJuego(
            @Valid @RequestBody JuegoDTO.Request request) {

        log.info("[game-service] POST /api/v1/juegos - nombre={}", request.getNombre());
        JuegoDTO.Response response = juegoService.crearJuego(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar catálogo de juegos", description = "Obtiene una lista de todos los juegos registrados, permitiendo filtrar opcionalmente por los que están activos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catálogo de juegos obtenido exitosamente")
    })
    public ResponseEntity<List<JuegoDTO.Response>> listarJuegos(
            @Parameter(description = "Interruptor booleano para retornar únicamente juegos activos en el sistema", example = "true")
            @RequestParam(defaultValue = "false") boolean soloActivos) {

        log.info("[game-service] GET /api/v1/juegos - soloActivos={}", soloActivos);
        List<JuegoDTO.Response> response = juegoService.listarJuegos(soloActivos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener juego por ID", description = "Busca y retorna los detalles técnicos de un juego específico mediante su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = JuegoDTO.Response.class))),
            @ApiResponse(responseCode = "404", description = "El juego solicitado no existe en los registros")
    })
    public ResponseEntity<JuegoDTO.Response> buscarPorId(
            @Parameter(description = "ID único incremental del videojuego", example = "1", required = true)
            @PathVariable Long id) {
        log.info("[game-service] GET /api/v1/juegos/{}", id);
        JuegoDTO.Response response = juegoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un juego", description = "Modifica los atributos editables (nombre, modalidad, límite de integrantes) de un juego existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = JuegoDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud de actualización con datos incorrectos"),
            @ApiResponse(responseCode = "404", description = "El videojuego a actualizar no fue localizado")
    })
    public ResponseEntity<JuegoDTO.Response> actualizarJuego(
            @Parameter(description = "ID del juego que se procederá a actualizar", example = "1", required = true) @PathVariable Long id,
            @Valid @RequestBody JuegoDTO.Request request) {

        log.info("[game-service] PUT /api/v1/juegos/{}", id);
        JuegoDTO.Response response = juegoService.actualizarJuego(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un juego (Borrado Lógico)", description = "Pasa el estado de un videojuego a inactivo por medio de su ID, impidiendo nuevas inscripciones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego desactivado exitosamente del catálogo"),
            @ApiResponse(responseCode = "404", description = "El juego con el ID provisto no existe")
    })
    public ResponseEntity<JuegoDTO.Response> desactivarJuego(
            @Parameter(description = "ID del juego que se dará de baja", example = "1", required = true)
            @PathVariable Long id) {
        log.info("[game-service] DELETE /api/v1/juegos/{}", id);
        JuegoDTO.Response response = juegoService.desactivarJuego(id);
        return ResponseEntity.ok(response);
    }
}