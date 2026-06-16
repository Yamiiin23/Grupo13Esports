package com.esports.tournament_service.exception;

import org.springframework.http.HttpStatus;

public class TorneoYaExisteException extends BusinessException {

    public TorneoYaExisteException(String nombre) {
        super("Ya existe un torneo registrado con el nombre: " + nombre, HttpStatus.BAD_REQUEST);
    }
}