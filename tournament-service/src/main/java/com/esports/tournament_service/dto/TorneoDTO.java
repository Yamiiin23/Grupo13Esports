package com.esports.tournament_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDateTime;

public class TorneoDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "El nombre del torneo es obligatorio")
        private String nombre;

        @NotNull(message = "El ID del juego asociado es obligatorio")
        @Positive(message = "El ID del juego debe ser un número válido")
        private Long gameId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String nombre;
        private Long gameId;
        private LocalDateTime fechaCreacion;

        public static Response fromEntity(com.esports.tournament_service.model.Torneo torneo) {
            return Response.builder()
                    .id(torneo.getId())
                    .nombre(torneo.getNombre())
                    .gameId(torneo.getGameId())
                    .fechaCreacion(torneo.getFechaCreacion())
                    .build();
        }
    }
}