package com.esports.tournament_service.controller;

import com.esports.tournament_service.assemblers.TorneoModelAssembler;
import com.esports.tournament_service.model.Torneo;
import com.esports.tournament_service.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(value = "/api/v2/torneos", produces = MediaTypes.HAL_JSON_VALUE)
public class TorneoControllerV2 {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private TorneoModelAssembler assembler;

    // 1. Obtener todos los torneos (con enlaces HATEOAS)
    @GetMapping
    public CollectionModel<EntityModel<Torneo>> getAll() {
        List<EntityModel<Torneo>> torneos = torneoRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(torneos,
                linkTo(methodOn(TorneoControllerV2.class).getAll()).withSelfRel());
    }

    // 2. Obtener un torneo específico por su ID
    @GetMapping("/{id}")
    public EntityModel<Torneo> getById(@PathVariable Long id) {
        Torneo torneo = torneoRepository.findById(id).orElseThrow();
        return assembler.toModel(torneo);
    }

    // 3. Obtener torneos filtrados por su Estado (Ej: INSCRIPCION, EN_CURSO)
    @GetMapping("/estado/{estado}")
    public CollectionModel<EntityModel<Torneo>> getTorneosByEstado(@PathVariable String estado) {
        List<EntityModel<Torneo>> torneos = torneoRepository.findAll().stream()
                .filter(t -> t.getEstado().name().equalsIgnoreCase(estado))
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(torneos,
                linkTo(methodOn(TorneoControllerV2.class).getTorneosByEstado(estado)).withSelfRel());
    }
}