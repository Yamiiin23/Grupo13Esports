package com.esports.registration_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "team-service", url = "localhost:8083/api/v1/equipos")
public interface TeamClient {
    @GetMapping("/{id}")
    Object getEquipoById(@PathVariable Long id);
}