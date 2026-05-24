package com.esports.ranking_service.dto;

import com.esports.ranking_service.model.Ranking;
import jakarta.validation.constraints.*;
import lombok.*;

public class RankingDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Request {
        @NotNull(message = "El ID del torneo es obligatorio")
        private Long torneoId;

        @NotNull(message = "El ID del participante es obligatorio")
        private Long participanteId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ActualizarRequest {
        @NotNull private Long ganadorId;
        @NotNull private Long perdedorId;
        @NotNull @Min(0) private Integer puntajeGanador;
        @NotNull @Min(0) private Integer puntajePerdedor;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long torneoId;
        private Long participanteId;
        private Integer puntos;
        private Integer victorias;
        private Integer derrotas;
        private Integer diferencia;
        private Integer posicion;

        public static Response fromEntity(Ranking r) {
            return Response.builder()
                    .id(r.getId())
                    .torneoId(r.getTorneoId())
                    .participanteId(r.getParticipanteId())
                    .puntos(r.getPuntos())
                    .victorias(r.getVictorias())
                    .derrotas(r.getDerrotas())
                    .diferencia(r.getDiferencia())
                    .posicion(r.getPosicion())
                    .build();
        }
    }
}