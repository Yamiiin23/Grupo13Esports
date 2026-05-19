package com.esports.gameservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JuegoNotFoundException extends RuntimeException {
    public JuegoNotFoundException(Long id) {
        super("No se encontró el juego con ID: " + id);
    }
    public JuegoNotFoundException(String nombre) {
        super("No se encontró el juego con nombre: " + nombre);
    }
}
