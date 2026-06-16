package com.esports.teamservice.assemblers;

import com.esports.teamservice.controller.EquipoControllerV2;
import com.esports.teamservice.model.Equipo;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class EquipoModelAssembler implements RepresentationModelAssembler<Equipo, EntityModel<Equipo>> {

    @Override
    public EntityModel<Equipo> toModel(Equipo equipo) {
        return EntityModel.of(equipo,
                linkTo(methodOn(EquipoControllerV2.class).getById(equipo.getId())).withSelfRel(),
                linkTo(methodOn(EquipoControllerV2.class).getAll()).withRel("equipos"));
    }
}