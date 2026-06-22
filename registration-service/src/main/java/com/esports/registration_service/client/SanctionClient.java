package com.esports.registration_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Asumiendo que sanction-service corre en el puerto 8088 (cámbialo si es otro)
@FeignClient(name = "sanction-service", url = "localhost:8088/api/v1/sanciones")
public interface SanctionClient {
    @GetMapping("/usuario/{userId}") // O la ruta que maneje las sanciones por usuario
    Object getSancionByUsuario(@PathVariable Long userId);
}