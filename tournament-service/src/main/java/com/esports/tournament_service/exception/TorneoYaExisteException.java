package com.esports.tournament_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TorneoYaExisteException extends RuntimeException {
    public TorneoYaExisteException(String nombre) {
        super("Ya existe un torneo registrado con el nombre: " + nombre);
    }
}