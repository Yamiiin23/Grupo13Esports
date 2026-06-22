package com.esports.resultservice.client;

import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "match-service") // URL removida. Eureka resolverá automáticamente la dirección del servicio de partidas.
public interface MatchClient {

    @GetMapping("/api/v1/partidas/{id}")
    PartidaResumen obtenerPartida(@PathVariable("id") Long id);

    @Data @NoArgsConstructor @AllArgsConstructor
    class PartidaResumen {
        private Long id;
        private Long torneoId;
        private Long participanteAId;
        private Long participanteBId;
        private String ronda;
        private String estado;
        private boolean puedeIniciar;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    class EstadoRequest {
        private String estado;
        private String observaciones;
    }

    @PatchMapping("/api/v1/partidas/{id}/estado")
    PartidaResumen cambiarEstadoPartida(@PathVariable("id") Long id,
                                        @RequestBody EstadoRequest request);
}