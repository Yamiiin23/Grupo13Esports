package com.esports.ranking_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

public class RankingDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull(message = "El ID del torneo es obligatorio")
        private Long torneoId;

        @NotNull(message = "El ID del participante es obligatorio")
        private Long participanteId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActualizarRequest {
        @NotNull(message = "El ID del ganador es obligatorio")
        private Long ganadorId;

        @NotNull(message = "El ID del perdedor es obligatorio")
        private Long perdedorId;

        private int puntajeGanador;
        private int puntajePerdedor;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long torneoId;
        private Long participanteId;
        private Integer puntos;
        private Integer victorias;
        private Integer derrotas;
        private Integer diferencia;
        private Integer posicion;

        public static Response fromEntity(com.esports.ranking_service.model.Ranking ranking) {
            return Response.builder()
                    .id(ranking.getId())
                    .torneoId(ranking.getTorneoId())
                    .participanteId(ranking.getParticipanteId())
                    .puntos(ranking.getPuntos())
                    .victorias(ranking.getVictorias())
                    .derrotas(ranking.getDerrotas())
                    .diferencia(ranking.getDiferencia())
                    .posicion(ranking.getPosicion())
                    .build();
        }
    }
}