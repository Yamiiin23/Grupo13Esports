package com.esports.tournament_service.exception;

import org.springframework.http.HttpStatus;

public class TorneoValidationException extends BusinessException {

    public TorneoValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}