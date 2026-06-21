package com.esports.match_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// nombre registrado en el application.yml de tournament-service y Eureka
@FeignClient(name = "tournament-service", path = "/api/v1/tournaments")
public interface TournamentServiceClient {

    @GetMapping("/{id}")
    Object obtenerTorneoPorId(@PathVariable("id") Long id);
}