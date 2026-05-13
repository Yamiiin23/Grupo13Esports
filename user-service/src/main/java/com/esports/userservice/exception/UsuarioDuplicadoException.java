package com.esports.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsuarioDuplicadoException extends RuntimeException {
    public UsuarioDuplicadoException(String campo, String valor) {
        super("Ya existe un usuario con " + campo + ": " + valor);
    }
}
