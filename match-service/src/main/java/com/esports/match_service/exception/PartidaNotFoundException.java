package com.esports.match_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PartidaNotFoundException extends RuntimeException {
    public PartidaNotFoundException(Long id) {
        super("No se encontró el enfrentamiento con ID: " + id);
    }
}