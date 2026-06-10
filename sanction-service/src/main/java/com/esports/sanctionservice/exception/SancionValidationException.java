package com.esports.sanctionservice.exception;

import org.springframework.http.HttpStatus;

public class SancionValidationException extends BusinessException {
    public SancionValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}