package com.esports.match_service.controller;

import com.esports.match_service.dto.PartidaDTO;
import com.esports.match_service.service.PartidaService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/partidas")
//cambio de guia 1
@Tag(name = "Partidas", description= "Operaciones relacionadas con la gestión y calendarizacion de enfrentamiento")
public class PartidaController {

    private static final Logger log = LoggerFactory.getLogger(PartidaController.class);
    private final PartidaService partidaService;

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @PostMapping
    //cambio de guia 1
    @Operation(summary = "Crear un nuevo enfrentamiento", description = "Registra una partida pendiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Partida creada exitosamente",
                    content = @Content(schema = @Schema(implementation = PartidaDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos o error de validacion logica.", content = @Content)
    })
    public ResponseEntity<PartidaDTO.Response> crearPartida(
            @Valid @RequestBody PartidaDTO.Request request) {
        log.info("[match-service] POST /api/v1/partidas - registrando nuevo enfrentamiento");
        PartidaDTO.Response response = partidaService.crearPartida(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    //otro cambio de la guia 1
    @Operation(summary = "Listar partidas", description = "Obtiene el listado completo de enfrentamientos, de ser posible filtrado por id de torneo")
    @ApiResponse(responseCode = "200", description = "Lista de partidas obtenida con éxito")
    public ResponseEntity<List<PartidaDTO.Response>> listarPartidas(
            @RequestParam(required = false) Long torneoId) {
        log.info("[match-service] GET /api/v1/partidas - filtro torneoId={}", torneoId);
        List<PartidaDTO.Response> response = partidaService.listarPartidas(torneoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    //otro cambio más
    @Operation(summary = "Obtener una partida por ID", description = "Buscar detalles de un enfrentamiento usando su ID")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Partida encontrada"),
    @ApiResponse(responseCode = "404", description = "No se encontró el enfrentamiento con el ID proporcionado", content = @Content)
    })
    public ResponseEntity<PartidaDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[match-service] GET /api/v1/partidas/{}", id);
        PartidaDTO.Response response = partidaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/resultado")
    //otro cambio
    @Operation(summary = "Actualizar resultado y estado de la partida", description = "Permite registrar el marcador final de un encuentro y modificar su estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcador actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de resultado invalidos"),
            @ApiResponse(responseCode = "404", description = "Partida no encontrada")
    })
    public ResponseEntity<PartidaDTO.Response> actualizarResultado(
            @PathVariable Long id,
            @Valid @RequestBody PartidaDTO.UpdateResultRequest request) {
        log.info("[match-service] PUT /api/v1/partidas/{}/resultado", id);
        PartidaDTO.Response response = partidaService.actualizarResultado(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    //ultimo cambio
    @Operation(summary = "Eliminar un enfrentamiento", description = "Remueve fisicamente el registro de la partida del sistema con su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Partida eliminada con éxito."),
            @ApiResponse(responseCode = "404", description = "La partida no existe")
    })
    public ResponseEntity<Void> eliminarPartida(@PathVariable Long id) {
        log.info("[match-service] DELETE /api/v2/partidas/{}", id);
        partidaService.eliminarPartida(id);
        return ResponseEntity.noContent().build();
    }
}