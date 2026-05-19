package com.esports.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(Long id) {
        super("No se encontró el usuario con ID: " + id);
    }
    public UsuarioNotFoundException(String valor) {
        super("No se encontró el usuario: " + valor);
    }
}
