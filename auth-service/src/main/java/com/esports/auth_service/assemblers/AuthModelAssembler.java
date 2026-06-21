package com.esports.auth_service.assemblers;

import com.esports.auth_service.controller.AuthControllerV2; // <-- Asegúrate de que el nombre del controlador coincida con el tuyo
import com.esports.auth_service.model.UsuarioAuth;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class AuthModelAssembler implements RepresentationModelAssembler<UsuarioAuth, EntityModel<UsuarioAuth>> {

    @Override
    public EntityModel<UsuarioAuth> toModel(UsuarioAuth auth) {
        return EntityModel.of(auth,
                linkTo(methodOn(AuthControllerV2.class).getById(auth.getId())).withSelfRel(),
                linkTo(methodOn(AuthControllerV2.class).getAll()).withRel("auths"));
    }
}