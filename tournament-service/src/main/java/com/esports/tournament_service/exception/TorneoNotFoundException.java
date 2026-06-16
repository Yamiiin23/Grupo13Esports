package com.esports.tournament_service.exception;

import org.springframework.http.HttpStatus;

public class TorneoNotFoundException extends BusinessException {

    public TorneoNotFoundException(Long id) {
        super("No se encontró el torneo con ID: " + id, HttpStatus.NOT_FOUND);
    }
}