package com.esports.userservice.controller;

import com.esports.userservice.assemblers.UsuarioModelAssembler;
import com.esports.userservice.model.Usuario;
import com.esports.userservice.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(value = "/api/v2/usuarios", produces = MediaTypes.HAL_JSON_VALUE)
public class UsuarioControllerV2 {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Usuario>> getAll() {
        List<EntityModel<Usuario>> usuarios = usuarioRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioControllerV2.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Usuario> getById(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        return assembler.toModel(usuario);
    }

    @GetMapping("/rol/{rol}")
    public CollectionModel<EntityModel<Usuario>> getUsuariosByRol(@PathVariable String rol) {
        List<EntityModel<Usuario>> usuarios = usuarioRepository.findAll().stream()
                .filter(u -> u.getRol().name().equalsIgnoreCase(rol))
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioControllerV2.class).getUsuariosByRol(rol)).withSelfRel());
    }
}