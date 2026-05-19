package com.esports.resultservice.dto;

import com.esports.resultservice.model.Resultado;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class ResultadoDTO {


    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Request {

        @NotNull(message = "El ID de la partida es obligatorio")
        @Positive(message = "El ID de la partida debe ser positivo")
        private Long partidaId;

        @NotNull(message = "El ID del ganador es obligatorio")
        @Positive(message = "El ID del ganador debe ser positivo")
        private Long ganadorId;

        @NotNull(message = "El puntaje A es obligatorio")
        @Min(value = 0, message = "El puntaje A no puede ser negativo")
        private Integer puntajeA;

        @NotNull(message = "El puntaje B es obligatorio")
        @Min(value = 0, message = "El puntaje B no puede ser negativo")
        private Integer puntajeB;

        @Size(max = 300, message = "Las observaciones no pueden superar 300 caracteres")
        private String observaciones;
    }


    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ValidacionRequest {

        @NotNull(message = "El estado de validación es obligatorio")
        private Resultado.EstadoValidacion estadoValidacion;

        @Size(max = 300)
        private String observaciones;
    }


    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long partidaId;
        private Long ganadorId;
        private Integer puntajeA;
        private Integer puntajeB;
        private String estadoValidacion;
        private boolean validado;
        private String observaciones;
        private LocalDateTime fechaRegistro;
        private LocalDateTime fechaActualizacion;

        public static Response fromEntity(Resultado r) {
            return Response.builder()
                    .id(r.getId())
                    .partidaId(r.getPartidaId())
                    .ganadorId(r.getGanadorId())
                    .puntajeA(r.getPuntajeA())
                    .puntajeB(r.getPuntajeB())
                    .estadoValidacion(r.getEstadoValidacion().name())
                    .validado(r.estaValidado())
                    .observaciones(r.getObservaciones())
                    .fechaRegistro(r.getFechaRegistro())
                    .fechaActualizacion(r.getFechaActualizacion())
                    .build();
        }
    }
}
