package com.esports.game_service.controller;

import com.esports.game_service.dto.JuegoDTO;
import com.esports.game_service.service.JuegoService;
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
@RequestMapping("/api/v1/juegos")
@Tag(name = "Juegos", description = "Operaciones relacionadas con la gestion de juegos")
public class JuegoController {

    private static final Logger log = LoggerFactory.getLogger(JuegoController.class);

    private final JuegoService juegoService;

    public JuegoController(JuegoService juegoService) {
        this.juegoService = juegoService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo juego", description = "Registra un nuevo juego en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Juego creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud de creacion invalida")
    })
    public ResponseEntity<JuegoDTO.Response> crearJuego(
            @Valid @RequestBody JuegoDTO.Request request) {

        log.info("[game-service] POST /api/v1/juegos - nombre={}", request.getNombre());
        JuegoDTO.Response response = juegoService.crearJuego(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar juegos", description = "Obtiene una lista de todos los juegos registrados, permite filtrar por los que estan activos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de juegos obtenida exitosamente")
    })
    public ResponseEntity<List<JuegoDTO.Response>> listarJuegos(
            @RequestParam(defaultValue = "false") boolean soloActivos) {

        log.info("[game-service] GET /api/v1/juegos - soloActivos={}", soloActivos);
        List<JuegoDTO.Response> response = juegoService.listarJuegos(soloActivos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener juego por ID", description = "Busca y retorna los detalles de un juego")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    public ResponseEntity<JuegoDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[game-service] GET /api/v1/juegos/{}", id);
        JuegoDTO.Response response = juegoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un juego", description = "Modifica los datos de un juego existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud de actualizacion invalida"),
            @ApiResponse(responseCode = "404", description = "Juego a actualizar no encontrado")
    })
    public ResponseEntity<JuegoDTO.Response> actualizarJuego(
            @PathVariable Long id,
            @Valid @RequestBody JuegoDTO.Request request) {

        log.info("[game-service] PUT /api/v1/juegos/{}", id);
        JuegoDTO.Response response = juegoService.actualizarJuego(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un juego", description = "Elimina (desactiva) un juego por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    public ResponseEntity<JuegoDTO.Response> desactivarJuego(@PathVariable Long id) {
        log.info("[game-service] DELETE /api/v1/juegos/{}", id);
        JuegoDTO.Response response = juegoService.desactivarJuego(id);
        return ResponseEntity.ok(response);
    }
}

