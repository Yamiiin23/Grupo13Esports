package com.esports.teamservice.client;

import com.esports.teamservice.dto.ClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "localhost:8081/api/v1/usuarios")
public interface UserClient {

    @GetMapping("/{id}/resumen")
    ClientDTO.UsuarioResumen findById(@PathVariable Long id);

}