package com.esports.registration_service.assemblers;

import com.esports.registration_service.controller.InscripcionControllerV2;
import com.esports.registration_service.model.Inscripcion;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class InscripcionModelAssembler implements RepresentationModelAssembler<Inscripcion, EntityModel<Inscripcion>> {

    @Override
    public EntityModel<Inscripcion> toModel(Inscripcion inscripcion) {
        return EntityModel.of(inscripcion,
                // Link hacia la inscripción específica
                linkTo(methodOn(InscripcionControllerV2.class).getById(inscripcion.getId())).withSelfRel(),
                // Link de vuelta a la lista general
                linkTo(methodOn(InscripcionControllerV2.class).getAll()).withRel("inscripciones")
        );
    }
}