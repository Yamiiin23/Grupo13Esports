package com.esports.teamservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${feign.client.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/api/v1/usuarios/{id}/resumen")
    ClientDTO.UsuarioResumen obtenerResumenUsuario(@PathVariable("id") Long id);
}
