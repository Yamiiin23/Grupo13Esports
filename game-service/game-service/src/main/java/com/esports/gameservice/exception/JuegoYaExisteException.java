package com.esports.gameservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class JuegoYaExisteException extends RuntimeException {
    public JuegoYaExisteException(String nombre) {
        super("Ya existe un juego registrado con el nombre: " + nombre);
    }
}
