package com.esports.teamservice.client;

import com.esports.teamservice.dto.ClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "game-service", url = "localhost:8082/api/v1/juegos")
public interface GameClient {

    @GetMapping("/{id}")
    ClientDTO.JuegoResumen findById(@PathVariable Long id);

}