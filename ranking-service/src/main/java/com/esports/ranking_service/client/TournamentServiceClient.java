package com.esports.ranking_service.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tournament-service", url = "${feign.client.tournament-service.url}")
public interface TournamentServiceClient {

    @GetMapping("/api/v1/torneos/{id}")
    TorneoResumen obtenerTorneoPorId(@PathVariable("id") Long id);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class TorneoResumen {
        private Long id;
        private String nombre;
        private String estado;
    }
}