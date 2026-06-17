package com.esports.ranking_service.assemblers;

import com.esports.ranking_service.controller.RankingControllerV2;
import com.esports.ranking_service.dto.RankingDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class RankingModelAssembler implements RepresentationModelAssembler<RankingDTO.Response, EntityModel<RankingDTO.Response>> {

    @Override
    public EntityModel<RankingDTO.Response> toModel(RankingDTO.Response ranking) {
        return EntityModel.of(ranking,
                linkTo(methodOn(RankingControllerV2.class).obtenerTabla(ranking.getTorneoId())).withRel("tabla_torneo"));

    }

}
