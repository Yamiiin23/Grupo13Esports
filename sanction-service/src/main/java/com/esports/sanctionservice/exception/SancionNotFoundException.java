package com.esports.sanctionservice.exception;

import org.springframework.http.HttpStatus;

public class SancionNotFoundException extends BusinessException {
    public SancionNotFoundException(Long id) {
        super("No se encontró la sanción con ID: " + id, HttpStatus.NOT_FOUND);
    }
}