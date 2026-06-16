package com.esports.tournament_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "Modelo de transferencia de datos para la gestión de Torneos")
public class TorneoDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estructura para solicitar la creación o edición de un torneo")
    public static class Request {

        @NotBlank(message = "El nombre del torneo es obligatorio")
        @Schema(description = "Nombre único del campeonato de eSports", example = "Copa Duoc UC 2026", requiredMode = Schema.RequiredMode.REQUIRED)
        private String nombre;

        @NotNull(message = "El ID del juego asociado es obligatorio")
        @Positive(message = "El ID del juego debe ser un número válido")
        @Schema(description = "Identificador del videojuego obtenido remetamente desde game-service", example = "105", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long gameId;

        @Min(value = 4, message = "El torneo debe permitir un mínimo de 4 equipos para ser competitivo")
        @Max(value = 128, message = "Por razones de infraestructura el máximo de equipos es 128")
        @Schema(description = "Cantidad máxima de cupos o equipos permitidos", example = "16", defaultValue = "16")
        private Integer maxParticipantes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estructura de respuesta con los datos detallados del torneo")
    public static class Response {

        @Schema(description = "ID auto-incremental en la base de datos", example = "1")
        private Long id;

        @Schema(description = "Nombre del campeonato", example = "Copa Duoc UC 2026")
        private String nombre;

        @Schema(description = "ID del juego asociado", example = "105")
        private Long gameId;

        @Schema(description = "Estado actual dentro de su ciclo de vida", example = "INSCRIPCION")
        private String estado;

        @Schema(description = "Cupos máximos configurados para el torneo", example = "16")
        private Integer maxParticipantes;

        @Schema(description = "Cantidad de equipos actualmente enrolados", example = "0")
        private Integer participantesActuales;

        @Schema(description = "Justificación registrada si el torneo fue dado de baja vía DELETE", example = "Cancelado por falta de equipos inscritos")
        private String motivoAnulacion;

        @Schema(description = "Fecha y hora exacta de registro en el sistema", example = "2026-06-16T10:15:30")
        private LocalDateTime fechaCreacion;

        public static Response fromEntity(com.esports.tournament_service.model.Torneo torneo) {
            return Response.builder()
                    .id(torneo.getId())
                    .nombre(torneo.getNombre())
                    .gameId(torneo.getGameId())
                    .estado(torneo.getEstado() != null ? torneo.getEstado().name() : null)
                    .maxParticipantes(torneo.getMaxParticipantes())
                    .participantesActuales(torneo.getParticipantesActuales())
                    .motivoAnulacion(torneo.getMotivoAnulacion())
                    .fechaCreacion(torneo.getFechaCreacion())
                    .build();
        }
    }
}