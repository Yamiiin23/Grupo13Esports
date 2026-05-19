package com.esports.rankingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RankingNotFoundException extends RuntimeException {
    public RankingNotFoundException(Long torneoId, Long participanteId) {
        super("No se encontró ranking para torneoId=" + torneoId + " y participanteId=" + participanteId);
    }
    public RankingNotFoundException(Long id) {
        super("No se encontró el registro de ranking con ID: " + id);
    }
}
