package com.esports.registration_service.dto;

import com.esports.registration_service.model.Inscripcion;
import lombok.*;
import java.time.LocalDateTime;

public class InscripcionDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long torneoId;
        private Long equipoId;
        private String estado;
        private String motivoCancelacion;
        private LocalDateTime fechaInscripcion;

        public static Response fromEntity(Inscripcion entity) {
            return Response.builder()
                    .id(entity.getId())
                    .torneoId(entity.getTorneoId())
                    .equipoId(entity.getEquipoId())
                    .estado(entity.getEstado().name())
                    .motivoCancelacion(entity.getMotivoCancelacion())
                    .fechaInscripcion(entity.getFechaInscripcion())
                    .build();
        }
    }
}