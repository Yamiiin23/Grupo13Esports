package com.esports.ranking_service.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "team-service")
public interface TeamClient {

    @GetMapping("/api/v1/equipos/{id}")
    EquipoResumen obtenerEquipoPorId(@PathVariable("id") Long id);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class EquipoResumen {
        private Long id;
        private String nombre;
    }
}