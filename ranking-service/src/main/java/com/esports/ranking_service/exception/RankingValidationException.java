package com.esports.ranking_service.exception;

import org.springframework.http.HttpStatus;

public class RankingValidationException extends BusinessException {
    public RankingValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}