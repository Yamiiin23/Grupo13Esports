package com.esports.sanctionservice.controller;

import com.esports.sanctionservice.assemblers.SancionModelAssembler;
import com.esports.sanctionservice.dto.SancionDTO;
import com.esports.sanctionservice.model.Sancion;
import com.esports.sanctionservice.service.SancionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/sanciones")
public class SancionControllerV2 {

    @Autowired
    private SancionService sancionService;

    @Autowired
    private SancionModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<SancionDTO.Response>> listarSanciones(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long equipoId,
            @RequestParam(required = false) Sancion.EstadoSancion estado) {

        List<EntityModel<SancionDTO.Response>> sanciones = sancionService.listarSanciones(usuarioId, equipoId, estado).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(sanciones,
                linkTo(methodOn(SancionControllerV2.class).listarSanciones(usuarioId, equipoId, estado)).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<SancionDTO.Response> buscarPorId(@PathVariable Long id) {
        SancionDTO.Response sancion = sancionService.buscarPorId(id);
        return assembler.toModel(sancion);
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<SancionDTO.Response>> crearSancion(@Valid @RequestBody SancionDTO.Request request) {
        SancionDTO.Response newSancion = sancionService.crearSancion(request);

        return ResponseEntity
                .created(linkTo(methodOn(SancionControllerV2.class).buscarPorId(newSancion.getId())).toUri())
                .body(assembler.toModel(newSancion));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<SancionDTO.Response>> actualizarSancion(
            @PathVariable Long id, @Valid @RequestBody SancionDTO.Request request) {

        SancionDTO.Response updatedSancion = sancionService.actualizarSancion(id, request);

        return ResponseEntity.ok(assembler.toModel(updatedSancion));
    }

    @PatchMapping(value = "/{id}/cerrar", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<SancionDTO.Response>> cerrarSancion(
            @PathVariable Long id, @Valid @RequestBody SancionDTO.EstadoRequest request) {

        SancionDTO.Response cerradaSancion = sancionService.cerrarSancion(id, request.getJustificacion());

        return ResponseEntity.ok(assembler.toModel(cerradaSancion));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<SancionDTO.Response>> anularSancionLogica(
            @PathVariable Long id, @RequestParam String justificacion) {

        SancionDTO.Response anuladaSancion = sancionService.anularSancionLogica(id, justificacion);

        return ResponseEntity.ok(assembler.toModel(anuladaSancion));
    }

    // --- Endpoints de Orquestación e Integración (Mantienen ResponseEntity clásico con EntityModel inline para coherencia) ---

    @GetMapping(value = "/verificar/usuario/{usuarioId}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<SancionDTO.VerificacionResponse>> verificarBloqueoUsuario(@PathVariable Long usuarioId) {
        SancionDTO.VerificacionResponse response = sancionService.verificarBloqueoUsuario(usuarioId);

        EntityModel<SancionDTO.VerificacionResponse> model = EntityModel.of(response,
                linkTo(methodOn(SancionControllerV2.class).verificarBloqueoUsuario(usuarioId)).withSelfRel());

        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/verificar/equipo/{equipoId}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<SancionDTO.VerificacionResponse>> verificarBloqueoEquipo(@PathVariable Long equipoId) {
        SancionDTO.VerificacionResponse response = sancionService.verificarBloqueoEquipo(equipoId);

        EntityModel<SancionDTO.VerificacionResponse> model = EntityModel.of(response,
                linkTo(methodOn(SancionControllerV2.class).verificarBloqueoEquipo(equipoId)).withSelfRel());

        return ResponseEntity.ok(model);
    }

    @PostMapping(value = "/cerrar-vencidas", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Map<String, Object>>> cerrarVencidas() {
        int total = sancionService.cerrarSancionesVencidas();

        EntityModel<Map<String, Object>> model = EntityModel.of(Map.of("sancionesCerradas", total),
                linkTo(methodOn(SancionControllerV2.class).cerrarVencidas()).withSelfRel(),
                linkTo(methodOn(SancionControllerV2.class).listarSanciones(null, null, null)).withRel("sanciones"));

        return ResponseEntity.ok(model);
    }
}

