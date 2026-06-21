package com.esports.auth_service.controller;

import com.esports.auth_service.assemblers.AuthModelAssembler;
import com.esports.auth_service.model.UsuarioAuth;
import com.esports.auth_service.repository.AuthRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/auth")
public class AuthControllerV2 {

    private final AuthRepository repository;
    private final AuthModelAssembler assembler;

    // Inyectamos el repositorio real y el Assembler de HATEOAS
    public AuthControllerV2(AuthRepository repository, AuthModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // AHORA SÍ: Buscamos en la BD y le pegamos los enlaces HATEOAS
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UsuarioAuth>>> getAll() {
        List<EntityModel<UsuarioAuth>> auths = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(auths));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioAuth>> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}