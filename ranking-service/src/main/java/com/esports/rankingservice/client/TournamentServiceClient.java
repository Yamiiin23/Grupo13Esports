package com.esports.rankingservice.client;

import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tournament-service", url = "${feign.client.tournament-service.url}")
public interface TournamentServiceClient {

    @GetMapping("/api/v1/torneos/{id}/resumen")
    TorneoResumen obtenerResumen(@PathVariable("id") Long id);

    @Data @NoArgsConstructor @AllArgsConstructor
    class TorneoResumen {
        private Long id;
        private String nombre;
        private String estado;
        private boolean aceptaInscripciones;
    }
}
