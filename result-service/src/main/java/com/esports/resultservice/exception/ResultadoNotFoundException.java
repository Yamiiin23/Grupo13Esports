package com.esports.resultservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ResultadoNotFoundException extends BusinessException {

    public ResultadoNotFoundException(Long id) {
        super("No se encontró el resultado con ID: " + id, HttpStatus.NOT_FOUND);
    }
}