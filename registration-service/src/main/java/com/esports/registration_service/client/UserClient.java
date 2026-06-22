package com.esports.registration_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "localhost:8081/api/v1/usuarios")
public interface UserClient {
    @GetMapping("/{id}")
    Object getUsuarioById(@PathVariable Long id);
}
