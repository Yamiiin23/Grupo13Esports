package com.esports.ranking_service.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "registration-service")
public interface RegistrationClient {

    @GetMapping("/api/v1/inscripciones/torneo/{torneoId}")
    List<InscripcionResumen> obtenerInscripcionesPorTorneo(@PathVariable("torneoId") Long torneoId);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class InscripcionResumen {
        private Long id;
        private Long torneoId;
        private Long equipoId;
        private String estado;
    }
}