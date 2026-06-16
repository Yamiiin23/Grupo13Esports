package com.esports.game_service.controller;

import com.esports.game_service.assemblers.JuegoModelAssembler;
import com.esports.game_service.model.Juego;
import com.esports.game_service.repository.JuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(value = "/api/v2/juegos", produces = MediaTypes.HAL_JSON_VALUE) // Exigido por la guía
public class JuegoControllerV2 {

    @Autowired
    private JuegoRepository juegoRepository;

    @Autowired
    private JuegoModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Juego>> getAll() {
        // 1. Buscamos todos los juegos
        List<EntityModel<Juego>> juegos = juegoRepository.findAll().stream()
                .map(assembler::toModel) // 2. Los transformamos con el Assembler
                .collect(Collectors.toList());

        // 3. Retornamos la colección con su propio link
        return CollectionModel.of(juegos,
                linkTo(methodOn(JuegoControllerV2.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Juego> getById(@PathVariable Long id) {
        Juego juego = juegoRepository.findById(id).orElseThrow();
        return assembler.toModel(juego);
    }

    @GetMapping("/genero/{genero}")
    public CollectionModel<EntityModel<Juego>> getJuegosByGenero(@PathVariable String genero) {
        // 1. Filtrar los juegos por género en el repositorio
        List<EntityModel<Juego>> juegos = juegoRepository.findAll().stream()
                .filter(j -> j.getGenero().equalsIgnoreCase(genero))
                .map(assembler::toModel)
                .collect(Collectors.toList());

        // 2. Retornar la colección con el link dinámico hacia este filtro específico
        return CollectionModel.of(juegos,
                linkTo(methodOn(JuegoControllerV2.class).getJuegosByGenero(genero)).withSelfRel());
    }
}