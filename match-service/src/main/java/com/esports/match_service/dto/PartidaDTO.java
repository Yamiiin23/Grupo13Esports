package com.esports.match_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PartidaDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull(message = "El ID del torneo es obligatorio")
        @Positive(message = "El ID del torneo debe ser un número positivo")
        private Long torneoId;

        @NotNull(message = "El ID del equipo local es obligatorio")
        @Positive(message = "El ID del equipo local debe ser un número positivo")
        private Long equipoLocalId;

        @NotNull(message = "El ID del equipo visitante es obligatorio")
        @Positive(message = "El ID del equipo visitante debe ser un número positivo")
        private Long equipoVisitanteId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateResultRequest {
        @NotBlank(message = "El resultado es obligatorio para actualizar el marcador")
        @Size(max = 20, message = "El formato de resultado no puede superar los 20 caracteres")
        private String resultado;

        @NotBlank(message = "El estado actual de la partida es obligatorio")
        private String estado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long torneoId;
        private Long equipoLocalId;
        private Long equipoVisitanteId;
        private String resultado;
        private String estado;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        // Usamos la ruta completa del paquete para que no falle la vinculación con el modelo
        public static Response fromEntity(com.esports.match_service.model.Partida partida) {
            return Response.builder()
                    .id(partida.getId())
                    .torneoId(partida.getTorneoId())
                    .equipoLocalId(partida.getEquipoLocalId())
                    .equipoVisitanteId(partida.getEquipoVisitanteId())
                    .resultado(partida.getResultado())
                    .estado(partida.getEstado() != null ? partida.getEstado().name() : "PENDIENTE")
                    .fechaCreacion(partida.getFechaCreacion())
                    .fechaActualizacion(partida.getFechaActualizacion())
                    .build();
        }
    }
}