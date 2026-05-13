package com.esports.teamservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
class EquipoYaExisteException extends RuntimeException {
    public EquipoYaExisteException(String nombre) {
        super("Ya existe un equipo con el nombre: " + nombre);
    }
}
