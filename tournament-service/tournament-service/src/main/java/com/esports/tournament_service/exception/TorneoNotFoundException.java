package com.esports.tournament_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TorneoNotFoundException extends RuntimeException {
    public TorneoNotFoundException(Long id) {
        super("No se encontró el torneo con ID: " + id);
    }
}