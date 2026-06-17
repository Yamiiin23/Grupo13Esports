package com.esports.sanctionservice.assemblers;

import com.esports.sanctionservice.controller.SancionControllerV2;
import com.esports.sanctionservice.dto.SancionDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class SancionModelAssembler implements RepresentationModelAssembler<SancionDTO.Response, EntityModel<SancionDTO.Response>> {

    @Override
    public EntityModel<SancionDTO.Response> toModel(SancionDTO.Response sancion) {
        return EntityModel.of(sancion,
                linkTo(methodOn(SancionControllerV2.class).buscarPorId(sancion.getId())).withSelfRel(),
                linkTo(methodOn(SancionControllerV2.class).listarSanciones(null, null, null)).withRel("sanciones"));
    }
}
