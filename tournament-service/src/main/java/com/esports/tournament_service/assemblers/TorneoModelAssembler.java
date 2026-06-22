package com.esports.tournament_service.assemblers;

import com.esports.tournament_service.model.Torneo;
// Importaremos el controlador en cuanto lo creemos en el siguiente paso
import com.esports.tournament_service.controller.TorneoControllerV2;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TorneoModelAssembler implements RepresentationModelAssembler<Torneo, EntityModel<Torneo>> {

    @Override
    public EntityModel<Torneo> toModel(Torneo torneo) {
        return EntityModel.of(torneo,
                // Link hacia el torneo específico (self)
                linkTo(methodOn(TorneoControllerV2.class).getById(torneo.getId())).withSelfRel(),
                // Link para volver a la lista general de torneos
                linkTo(methodOn(TorneoControllerV2.class).getAll()).withRel("torneos")
        );
    }
}