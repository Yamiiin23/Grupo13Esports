package com.esports.rankingservice.dto;

import com.esports.rankingservice.model.Ranking;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class RankingDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Request {

        @NotNull(message = "El ID del torneo es obligatorio")
        @Positive(message = "El ID del torneo debe ser positivo")
        private Long torneoId;

        @NotNull(message = "El ID del participante es obligatorio")
        @Positive(message = "El ID del participante debe ser positivo")
        private Long participanteId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ActualizarRequest {

        @NotNull(message = "El ID del ganador es obligatorio")
        @Positive
        private Long ganadorId;

        @NotNull(message = "El ID del perdedor es obligatorio")
        @Positive
        private Long perdedorId;

        @NotNull @Min(0)
        private Integer puntajeGanador;

        @NotNull @Min(0)
        private Integer puntajePerdedor;
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
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

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
                    .fechaCreacion(r.getFechaCreacion())
                    .fechaActualizacion(r.getFechaActualizacion())
                    .build();
        }
    }
}
