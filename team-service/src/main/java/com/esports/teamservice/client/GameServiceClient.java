package com.esports.teamservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "game-service", url = "${feign.client.game-service.url}")
public interface GameServiceClient {

    @GetMapping("/api/v1/juegos/{id}")
    ClientDTO.JuegoResumen obtenerJuego(@PathVariable("id") Long id);
}
