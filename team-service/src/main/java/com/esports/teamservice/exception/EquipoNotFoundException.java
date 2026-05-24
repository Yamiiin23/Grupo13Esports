package com.esports.teamservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EquipoNotFoundException extends RuntimeException {
    public EquipoNotFoundException(Long id) {
        super("No se encontró el equipo con ID: " + id);
    }
}
