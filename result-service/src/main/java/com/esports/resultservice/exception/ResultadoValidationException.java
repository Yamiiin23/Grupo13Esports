package com.esports.resultservice.exception;

import org.springframework.http.HttpStatus;

public class ResultadoValidationException extends BusinessException {

    public ResultadoValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}