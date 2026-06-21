package com.esports.match_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "registration-service", path = "/api/v1/inscripciones")
public interface RegistrationServiceClient {

    @GetMapping("/verificar-equipos")
    boolean verificarEquiposInscritos(@RequestParam Long torneoId, @RequestParam Long localId, @RequestParam Long visitanteId);
}
