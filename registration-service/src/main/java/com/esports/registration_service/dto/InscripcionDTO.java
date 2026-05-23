package com.esports.registration_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDateTime;

public class InscripcionDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull(message = "El ID del torneo es obligatorio")
        @Positive(message = "El ID del torneo debe ser un número positivo")
        private Long torneoId;

        @NotNull(message = "El ID del equipo es obligatorio")
        @Positive(message = "El ID del equipo debe ser un número positivo")
        private Long equipoId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatusRequest {
        @NotBlank(message = "El estado es obligatorio para actualizar")
        private String estado;
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
        private LocalDateTime fechaInscripcion;

        public static Response fromEntity(com.esports.registration_service.model.Inscripcion inscripcion) {
            return Response.builder()
                    .id(inscripcion.getId())
                    .torneoId(inscripcion.getTorneoId())
                    .equipoId(inscripcion.getEquipoId())
                    .estado(inscripcion.getEstado().name())
                    .fechaInscripcion(inscripcion.getFechaInscripcion())
                    .build();
        }
    }
}