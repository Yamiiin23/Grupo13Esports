package com.esports.ranking_service.exception;

import org.springframework.http.HttpStatus;

public class RankingNotFoundException extends BusinessException {
    public RankingNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}