package com.esports.resultservice.assemblers;

import com.esports.resultservice.controller.ResultadoControllerV2;
import com.esports.resultservice.dto.ResultadoDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ResultadoModelAssembler implements RepresentationModelAssembler<ResultadoDTO.Response, EntityModel<ResultadoDTO.Response>> {

    @Override
    public EntityModel<ResultadoDTO.Response> toModel(ResultadoDTO.Response resultado) {
        return EntityModel.of(resultado,
                linkTo(methodOn(ResultadoControllerV2.class).buscarPorId(resultado.getId())).withSelfRel(),
                linkTo(methodOn(ResultadoControllerV2.class).listarResultados(null)).withRel("resultados"));
    }
}
