package com.esports.resultservice.exception;

import org.springframework.http.HttpStatus;

public class ServicioExternoException extends BusinessException {

    public ServicioExternoException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}