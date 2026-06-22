package com.esports.registration_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tournament-service", url = "localhost:8084/api/v1/torneos")
public interface TournamentClient {
    @GetMapping("/{id}")
    Object getTorneoById(@PathVariable Long id);
}