package com.esports.teamservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MiembroDuplicadoException extends RuntimeException {
    public MiembroDuplicadoException(Long usuarioId, Long equipoId) {
        super("El usuario ID=" + usuarioId + " ya es miembro del equipo ID=" + equipoId);
    }
}
