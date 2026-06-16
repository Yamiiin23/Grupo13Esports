package com.esports.registration_service.exception;

import org.springframework.http.HttpStatus;

public class InscripcionNotFoundException extends BusinessException {
    public InscripcionNotFoundException(Long id) {
        super("No se encontró la solicitud de inscripción con ID: " + id, HttpStatus.NOT_FOUND);
    }
}