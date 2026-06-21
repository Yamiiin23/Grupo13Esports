package com.esports.match_service.controller;

import com.esports.match_service.assemblers.PartidaModelAssemblers;
import com.esports.match_service.dto.PartidaDTO;
import com.esports.match_service.service.PartidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/partidas")
@Tag(name = "Partidas", description= "Operaciones relacionadas con la gestión y calendarizacion de enfrentamiento")
public class PartidaControllerV2 {

    private static final Logger log = LoggerFactory.getLogger(PartidaControllerV2.class);

    private final PartidaService partidaService;

    @Autowired
    private PartidaModelAssemblers assembler; // Inyección del ensamblador al estilo de tus compañeros

    public PartidaControllerV2(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Crear un nuevo enfrentamiento", description = "Registra una partida pendiente")
    public ResponseEntity<EntityModel<PartidaDTO.Response>> crearPartida(
            @Valid @RequestBody PartidaDTO.Request request) {
        log.info("[match-service] POST /api/v1/partidas - registrando nuevo enfrentamiento");

        PartidaDTO.Response response = partidaService.crearPartida(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(response));
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar partidas", description = "Obtiene el listado completo de enfrentamientos con soporte HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<PartidaDTO.Response>>> listarPartidas(
            @RequestParam(required = false) Long torneoId) {
        log.info("[match-service] GET /api/v1/partidas - filtro torneoId={}", torneoId);

        List<EntityModel<PartidaDTO.Response>> partidas = partidaService.listarPartidas(torneoId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(partidas, linkTo(methodOn(PartidaController.class).listarPartidas(torneoId)).withSelfRel())
        );
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtener una partida por ID", description = "Buscar detalles de un enfrentamiento usando su ID")
    public ResponseEntity<EntityModel<PartidaDTO.Response>> buscarPorId(@PathVariable Long id) {
        log.info("[match-service] GET /api/v1/partidas/{}", id);

        PartidaDTO.Response response = partidaService.buscarPorId(id);

        return ResponseEntity.ok(assembler.toModel(response));
    }

    @PutMapping(value = "/{id}/resultado", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Actualizar resultado y estado de la partida", description = "Permite registrar el marcador final de un encuentro")
    public ResponseEntity<EntityModel<PartidaDTO.Response>> actualizarResultado(
            @PathVariable Long id,
            @Valid @RequestBody PartidaDTO.UpdateResultRequest request) {
        log.info("[match-service] PUT /api/v1/partidas/{}/resultado", id);

        PartidaDTO.Response response = partidaService.actualizarResultado(id, request);

        return ResponseEntity.ok(assembler.toModel(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un enfrentamiento", description = "Remueve fisicamente el registro de la partida")
    public ResponseEntity<Void> eliminarPartida(@PathVariable Long id) {
        log.info("[match-service] DELETE /api/v1/partidas/{}", id);
        partidaService.eliminarPartida(id);
        return ResponseEntity.noContent().build();
    }
}