package com.esports.sanctionservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SancionNotFoundException extends RuntimeException {
    public SancionNotFoundException(Long id) {
        super("No se encontró la sanción con ID: " + id);
    }
}
