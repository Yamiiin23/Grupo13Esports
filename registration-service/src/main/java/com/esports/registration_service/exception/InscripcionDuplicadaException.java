package com.esports.registration_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InscripcionDuplicadaException extends RuntimeException {
    public InscripcionDuplicadaException(Long torneoId, Long equipoId) {
        super("El equipo con ID " + equipoId + " ya se encuentra inscrito en el torneo ID " + torneoId);
    }
}