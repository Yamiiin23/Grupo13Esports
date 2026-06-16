package com.esports.registration_service.exception;

import org.springframework.http.HttpStatus;

public class InscripcionDuplicadaException extends BusinessException {
    public InscripcionDuplicadaException(Long torneoId, Long equipoId) {
        super("El equipo con ID " + equipoId + " ya cuenta con una inscripción activa en el torneo ID " + torneoId, HttpStatus.BAD_REQUEST);
    }
}