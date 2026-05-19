package com.esports.rankingservice.client;

import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "result-service", url = "${feign.client.result-service.url}")
public interface ResultServiceClient {

    @GetMapping("/api/v1/resultados/partida/{partidaId}")
    ResultadoResumen obtenerResultadoPorPartida(@PathVariable("id") Long partidaId);

    @Data @NoArgsConstructor @AllArgsConstructor
    class ResultadoResumen {
        private Long id;
        private Long partidaId;
        private Long ganadorId;
        private Integer puntajeA;
        private Integer puntajeB;
        private String estadoValidacion;
        private boolean validado;
    }
}
