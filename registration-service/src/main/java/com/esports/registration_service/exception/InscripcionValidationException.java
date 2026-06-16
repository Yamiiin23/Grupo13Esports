package com.esports.registration_service.exception;

import org.springframework.http.HttpStatus;

public class InscripcionValidationException extends BusinessException {
    public InscripcionValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}