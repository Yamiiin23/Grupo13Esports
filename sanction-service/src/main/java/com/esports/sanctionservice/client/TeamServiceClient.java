package com.esports.sanctionservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "team-service", url = "${feign.client.team-service.url}")
public interface TeamServiceClient {

    @GetMapping("/api/v1/equipos/{id}")
    EquipoResumen obtenerEquipo(@PathVariable("id") Long id);

    @Data @NoArgsConstructor @AllArgsConstructor
    class EquipoResumen {
        private Long id;
        private String nombre;
        private String estado;
        private boolean puedeInscribirse;
    }
}
