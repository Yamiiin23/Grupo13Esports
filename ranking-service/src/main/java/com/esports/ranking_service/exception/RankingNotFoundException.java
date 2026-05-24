package com.esports.ranking_service.exception;

public class RankingNotFoundException extends RuntimeException {
    public RankingNotFoundException(Long torneoId, Long participanteId) {
        super("No se encontró ranking para torneoId=" + torneoId + " y participanteId=" + participanteId);
    }
    public RankingNotFoundException(Long id) {
        super("No se encontró el ranking con ID: " + id);
    }
}