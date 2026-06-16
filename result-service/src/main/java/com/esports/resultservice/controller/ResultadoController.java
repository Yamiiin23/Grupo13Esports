package com.esports.resultservice.controller;

import com.esports.resultservice.dto.ResultadoDTO;
import com.esports.resultservice.model.Resultado;
import com.esports.resultservice.service.ResultadoService;
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
@RequestMapping("/api/v1/resultados")
@Tag(name = "Resultados", description = "Gestion de resultados")
public class ResultadoController {

    private static final Logger log = LoggerFactory.getLogger(ResultadoController.class);

    private final ResultadoService resultadoService;

    public ResultadoController(ResultadoService resultadoService) {
        this.resultadoService = resultadoService;
    }

    @PostMapping
    @Operation(summary = "Registrar un resultado", description = "Crea un nuevo registro de resultado para una partida")
    @ApiResponse(responseCode = "201", description = "Resultado creado exitosamente")
    public ResponseEntity<ResultadoDTO.Response> crearResultado(
            @Valid @RequestBody ResultadoDTO.Request request) {
        log.info("[result-service] POST /api/v1/resultados");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resultadoService.crearResultado(request));
    }

    @GetMapping
    @Operation(summary = "Listar resultados", description = "Obtiene todos los resultados, se puede filtrar por su estado de validacion")
    public ResponseEntity<List<ResultadoDTO.Response>> listarResultados(
            @RequestParam(required = false) Resultado.EstadoValidacion estado) {
        log.info("[result-service] GET /api/v1/resultados");
        return ResponseEntity.ok(resultadoService.listarResultados(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Obtiene los detalles de un resultado por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado encontrado"),
            @ApiResponse(responseCode = "404", description = "Resultado no encontrado")
    })
    public ResponseEntity<ResultadoDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[result-service] GET /api/v1/resultados/{}", id);
        return ResponseEntity.ok(resultadoService.buscarPorId(id));
    }

    @GetMapping("/partida/{partidaId}")
    @Operation(summary = "Buscar por partida", description = "Obtiene el resultado vinculado a un ID de partida")
    public ResponseEntity<ResultadoDTO.Response> buscarPorPartida(@PathVariable Long partidaId) {
        log.info("[result-service] GET /api/v1/resultados/partida/{}", partidaId);
        return ResponseEntity.ok(resultadoService.buscarPorPartida(partidaId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar resultado", description = "Modifica los datos de un resultado existente")
    public ResponseEntity<ResultadoDTO.Response> actualizarResultado(
            @PathVariable Long id,
            @Valid @RequestBody ResultadoDTO.Request request) {
        log.info("[result-service] PUT /api/v1/resultados/{}", id);
        return ResponseEntity.ok(resultadoService.actualizarResultado(id, request));
    }

    @PatchMapping("/{id}/validacion")
    @Operation(summary = "Cambiar validacion", description = "Actualiza el estado de validacion y observaciones de un resultado")
    public ResponseEntity<ResultadoDTO.Response> cambiarValidacion(
            @PathVariable Long id,
            @Valid @RequestBody ResultadoDTO.ValidacionRequest request) {
        log.info("[result-service] PATCH /api/v1/resultados/{}/validacion → {}", id, request.getEstadoValidacion());
        return ResponseEntity.ok(resultadoService.cambiarValidacion(id, request));
    }
}
