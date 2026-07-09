package com.esports.match_service.assemblers;

import com.esports.match_service.controller.PartidaController;
import com.esports.match_service.dto.PartidaDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@Component
public class PartidaModelAssemblers implements RepresentationModelAssembler<PartidaDTO.Response, EntityModel<PartidaDTO.Response>> {

    @Override
    public EntityModel<PartidaDTO.Response> toModel(PartidaDTO.Response partida) {
        EntityModel<PartidaDTO.Response> model = EntityModel.of(partida,
                linkTo(methodOn(PartidaController.class).buscarPorId(partida.getId())).withSelfRel(),

                linkTo(methodOn(PartidaController.class).listarPartidas(partida.getTorneoId())).withRel("partidas_torneo")
        );
        if ("PENDIENTE".equals(partida.getEstado()) || "EN_CURSO".equals(partida.getEstado())) {
            model.add(linkTo(methodOn(PartidaController.class).actualizarResultado(partida.getId(), null)).withRel("registrar_resultado"));
        }

        return model;
    }
}