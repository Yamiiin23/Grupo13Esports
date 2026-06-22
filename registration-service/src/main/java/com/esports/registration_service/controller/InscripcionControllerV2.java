package com.esports.registration_service.controller;

import com.esports.registration_service.assemblers.InscripcionModelAssembler;
import com.esports.registration_service.model.Inscripcion;
import com.esports.registration_service.repository.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(value = "/api/v2/inscripciones", produces = MediaTypes.HAL_JSON_VALUE)
public class InscripcionControllerV2 {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private InscripcionModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Inscripcion>> getAll() {
        List<EntityModel<Inscripcion>> inscripciones = inscripcionRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(inscripciones,
                linkTo(methodOn(InscripcionControllerV2.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Inscripcion> getById(@PathVariable Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id).orElseThrow();
        return assembler.toModel(inscripcion);
    }

    @GetMapping("/estado/{estado}")
    public CollectionModel<EntityModel<Inscripcion>> getByEstado(@PathVariable String estado) {
        List<EntityModel<Inscripcion>> inscripciones = inscripcionRepository.findAll().stream()
                .filter(i -> i.getEstado().name().equalsIgnoreCase(estado))
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(inscripciones,
                linkTo(methodOn(InscripcionControllerV2.class).getByEstado(estado)).withSelfRel());
    }

    @GetMapping("/torneo/{torneoId}")
    public CollectionModel<EntityModel<Inscripcion>> getByTorneo(@PathVariable Long torneoId) {
        List<EntityModel<Inscripcion>> inscripciones = inscripcionRepository.findByTorneoId(torneoId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(inscripciones,
                linkTo(methodOn(InscripcionControllerV2.class).getByTorneo(torneoId)).withSelfRel());
    }
}