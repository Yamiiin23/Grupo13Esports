package com.esports.gameservice.dto;

import com.esports.gameservice.model.Juego;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class JuegoDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "El nombre del juego es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        private String nombre;

        @NotBlank(message = "El género es obligatorio")
        @Size(max = 80, message = "El género no puede superar 80 caracteres")
        private String genero;

        @NotBlank(message = "La modalidad es obligatoria")
        @Size(max = 80, message = "La modalidad no puede superar 80 caracteres")
        private String modalidad;

        @NotNull(message = "La cantidad de jugadores por equipo es obligatoria")
        @Positive(message = "La cantidad de jugadores por equipo debe ser un número positivo")
        private Integer jugadoresPorEquipo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String nombre;
        private String genero;
        private String modalidad;
        private Integer jugadoresPorEquipo;
        private String estado;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        public static Response fromEntity(Juego juego) {
            return Response.builder()
                    .id(juego.getId())
                    .nombre(juego.getNombre())
                    .genero(juego.getGenero())
                    .modalidad(juego.getModalidad())
                    .jugadoresPorEquipo(juego.getJugadoresPorEquipo())
                    .estado(juego.getEstado().name())
                    .fechaCreacion(juego.getFechaCreacion())
                    .fechaActualizacion(juego.getFechaActualizacion())
                    .build();
        }
    }
}
