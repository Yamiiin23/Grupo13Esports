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
@Tag(name = "Juegos", description = "Endpoints para la gestion del catalogo de videojuegos")
public class JuegoController {

    private static final Logger log = LoggerFactory.getLogger(JuegoController.class);
    private final JuegoService juegoService;

    public JuegoController(JuegoService juegoService) {
        this.juegoService = juegoService;
    }

    @PostMapping
    @Operation(summary = "Crear un juego", description = "Registra un nuevo videojuego competitivo en la plataforma")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Juego creado con exito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada incorrectos")
    })
    public ResponseEntity<JuegoDTO.Response> crearJuego(@Valid @RequestBody JuegoDTO.Request request) {
        log.info("Peticion REST para crear juego: {}", request.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(juegoService.crearJuego(request));
    }

    @GetMapping
    @Operation(summary = "Listar juegos", description = "Retorna el catalogo completo, permitiendo filtrar por los que estan activos")
    public ResponseEntity<List<JuegoDTO.Response>> listarJuegos(
            @RequestParam(defaultValue = "false") boolean soloActivos) {
        log.info("Peticion REST para listar juegos - soloActivos: {}", soloActivos);
        return ResponseEntity.ok(juegoService.listarJuegos(soloActivos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener juego por ID")
    public ResponseEntity<JuegoDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("Peticion REST para buscar juego ID: {}", id);
        return ResponseEntity.ok(juegoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un juego")
    public ResponseEntity<JuegoDTO.Response> actualizarJuego(
            @PathVariable Long id,
            @Valid @RequestBody JuegoDTO.Request request) {
        log.info("Peticion REST para actualizar juego ID: {}", id);
        return ResponseEntity.ok(juegoService.actualizarJuego(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un juego (Baja logica)")
    public ResponseEntity<JuegoDTO.Response> desactivarJuego(@PathVariable Long id) {
        log.info("Peticion REST para dar de baja juego ID: {}", id);
        return ResponseEntity.ok(juegoService.desactivarJuego(id));
    }
}