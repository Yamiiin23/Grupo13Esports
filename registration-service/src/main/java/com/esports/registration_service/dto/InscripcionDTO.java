package com.esports.registration_service.dto;

import com.esports.registration_service.model.Inscripcion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

public class InscripcionDTO {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull(message = "El ID del torneo es obligatorio")
        private Long torneoId;

        @NotNull(message = "El ID del equipo es obligatorio")
        private Long equipoId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatusRequest {
        @NotBlank(message = "El estado no puede estar vacío")
        private String estado; // Recibe: PENDIENTE, ACEPTADA o RECHAZADA
    }

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