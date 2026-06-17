package com.esports.resultservice.controller;

import com.esports.resultservice.assemblers.ResultadoModelAssembler;
import com.esports.resultservice.dto.ResultadoDTO;
import com.esports.resultservice.model.Resultado;
import com.esports.resultservice.service.ResultadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/resultados")
public class ResultadoControllerV2 {

    @Autowired
    private ResultadoService resultadoService;

    @Autowired
    private ResultadoModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<ResultadoDTO.Response>> listarResultados(
            @RequestParam(required = false) Resultado.EstadoValidacion estado) {

        List<EntityModel<ResultadoDTO.Response>> resultados = resultadoService.listarResultados(estado).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(resultados,
                linkTo(methodOn(ResultadoControllerV2.class).listarResultados(estado)).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<ResultadoDTO.Response> buscarPorId(@PathVariable Long id) {
        return assembler.toModel(resultadoService.buscarPorId(id));
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ResultadoDTO.Response>> crearResultado(
            @Valid @RequestBody ResultadoDTO.Request request) {

        ResultadoDTO.Response nuevo = resultadoService.crearResultado(request);
        return ResponseEntity
                .created(linkTo(methodOn(ResultadoControllerV2.class).buscarPorId(nuevo.getId())).toUri())
                .body(assembler.toModel(nuevo));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ResultadoDTO.Response>> actualizarResultado(
            @PathVariable Long id, @Valid @RequestBody ResultadoDTO.Request request) {

        return ResponseEntity.ok(assembler.toModel(resultadoService.actualizarResultado(id, request)));
    }

    @PatchMapping(value = "/{id}/validacion", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ResultadoDTO.Response>> cambiarValidacion(
            @PathVariable Long id, @Valid @RequestBody ResultadoDTO.ValidacionRequest request) {

        return ResponseEntity.ok(assembler.toModel(resultadoService.cambiarValidacion(id, request)));
    }
}
