package com.esports.ranking_service.controller;

import com.esports.ranking_service.assemblers.RankingModelAssembler;
import com.esports.ranking_service.dto.RankingDTO;
import com.esports.ranking_service.service.RankingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/rankings")
public class RankingControllerV2 {

    @Autowired
    private RankingService rankingService;

    @Autowired
    private RankingModelAssembler assembler;

    @GetMapping(value = "/torneo/{torneoId}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<RankingDTO.Response>> obtenerTabla(@PathVariable Long torneoId){
        List<EntityModel<RankingDTO.Response>> rankings = rankingService.obtenerTabla(torneoId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(rankings,
                linkTo(methodOn(RankingControllerV2.class).obtenerTabla(torneoId)).withSelfRel());
    }

    @PostMapping(value = "/registrar", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<RankingDTO.Response>> registrarParticipante(@Valid @RequestBody RankingDTO.Request request) {
        RankingDTO.Response newRanking = rankingService.registrarParticipante(request);

        return ResponseEntity
                .created(linkTo(methodOn(RankingControllerV2.class).obtenerTabla(newRanking.getTorneoId())).toUri())
                .body(assembler.toModel(newRanking));
    }

    @PutMapping(value = "/torneo/{torneoId}/resultado", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<RankingDTO.Response>>> actualizarResultado(
            @PathVariable Long torneoId,
            @Valid @RequestBody RankingDTO.ActualizarRequest request) {

        List<RankingDTO.Response> actualizados = rankingService.actualizarConResultado(torneoId, request);

        List<EntityModel<RankingDTO.Response>> rankingsModel = actualizados.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(CollectionModel.of(rankingsModel,
                        linkTo(methodOn(RankingControllerV2.class).actualizarResultado(torneoId, request)).withSelfRel()));
    }
}
