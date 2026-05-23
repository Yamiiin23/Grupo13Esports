package com.esports.registration_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InscripcionNotFoundException extends RuntimeException {
    public InscripcionNotFoundException(Long id) {
        super("No se encontró la inscripción con ID: " + id);
    }
}