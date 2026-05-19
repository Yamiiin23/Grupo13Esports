package com.esports.resultservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResultadoNotFoundException extends RuntimeException {
    public ResultadoNotFoundException(Long id) {
        super("No se encontró el resultado con ID: " + id);
    }
}
