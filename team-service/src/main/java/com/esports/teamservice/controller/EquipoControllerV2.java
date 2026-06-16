package com.esports.teamservice.controller; // ¡OJO! Deja el tuyo si es diferente (ej: teamservice sin guion bajo)

import com.esports.teamservice.assemblers.EquipoModelAssembler; // Asegúrate de que apunte a tu carpeta real
import com.esports.teamservice.model.Equipo;
import com.esports.teamservice.repository.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(value = "/api/v2/equipos", produces = MediaTypes.HAL_JSON_VALUE)
public class EquipoControllerV2 {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private EquipoModelAssembler assembler;

    // 1. Listado general de todos los equipos
    @GetMapping
    public CollectionModel<EntityModel<Equipo>> getAll() {
        List<EntityModel<Equipo>> equipos = equipoRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(equipos,
                linkTo(methodOn(EquipoControllerV2.class).getAll()).withSelfRel());
    }

    // 2. Búsqueda de un único equipo por ID (Esta es la que fallaba)
    @GetMapping("/{id}")
    public EntityModel<Equipo> getById(@PathVariable Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo con ID " + id + " no encontrado"));

        return assembler.toModel(equipo);
    }

    // 3. Filtro personalizado por Estado
    @GetMapping("/estado/{estado}")
    public CollectionModel<EntityModel<Equipo>> getEquiposByEstado(@PathVariable String estado) {
        List<EntityModel<Equipo>> equipos = equipoRepository.findAll().stream()
                .filter(e -> e.getEstado().name().equalsIgnoreCase(estado))
                // Nota: Si te marca rojo ".name()", simplemente bórralo para que quede e.getEstado().equalsIgnoreCase(estado)
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(equipos,
                linkTo(methodOn(EquipoControllerV2.class).getEquiposByEstado(estado)).withSelfRel());
    }
}