package com.esports.game_service.assemblers;

import com.esports.game_service.controller.JuegoControllerV2;
import com.esports.game_service.model.Juego;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class JuegoModelAssembler implements RepresentationModelAssembler<Juego, EntityModel<Juego>> {

    @Override
    public EntityModel<Juego> toModel(Juego juego) {
        // Esto añade los links de "self" (él mismo) y "juegos" (la lista completa) a cada JSON
        return EntityModel.of(juego,
                linkTo(methodOn(JuegoControllerV2.class).getById(juego.getId())).withSelfRel(),
                linkTo(methodOn(JuegoControllerV2.class).getAll()).withRel("juegos"));
    }
}