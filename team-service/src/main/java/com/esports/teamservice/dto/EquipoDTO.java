package com.esports.teamservice.dto;

import com.esports.teamservice.model.Equipo;
import com.esports.teamservice.model.MiembroEquipo;
import io.swagger.v3.oas.annotations.media.Schema; // <-- NUEVA IMPORTACIÓN DE SWAGGER AGREGADA
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Clase contenedora de los modelos de transferencia (DTO) para la gestión de equipos")
public class EquipoDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estructura requerida para solicitar la creación o edición de una escuadra")
    public static class Request {

        @NotBlank(message = "El nombre del equipo es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @Schema(description = "Nombre oficial de la escuadra competitiva", example = "KRÜ Esports")
        private String nombre;

        @NotNull(message = "El ID del capitán es obligatorio")
        @Positive(message = "El ID del capitán debe ser un número positivo")
        @Schema(description = "ID único del usuario que asumirá la capitanía (Se validará inter-servicio)", example = "15")
        private Long capitanId;

        @NotNull(message = "El ID del juego principal es obligatorio")
        @Positive(message = "El ID del juego debe ser un número positivo")
        @Schema(description = "ID único del videojuego en el que competirá la escuadra", example = "2")
        private Long juegoPrincipalId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estructura requerida para solicitar la adición de un nuevo jugador a un equipo")
    public static class MiembroRequest {

        @NotNull(message = "El ID del usuario es obligatorio")
        @Positive(message = "El ID del usuario debe ser un número positivo")
        @Schema(description = "ID único del usuario/jugador que se unirá a las filas del equipo", example = "42")
        private Long usuarioId;

        @Schema(description = "Rol estratégico/posicional asignado al jugador dentro de la escuadra", example = "TITULAR")
        private MiembroEquipo.RolEnEquipo rolDentroEquipo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Modelo de respuesta que representa los detalles de un miembro del equipo")
    public static class MiembroResponse {

        @Schema(description = "ID único del registro de membresía", example = "1")
        private Long id;

        @Schema(description = "ID del usuario asociado al miembro", example = "42")
        private Long usuarioId;

        @Schema(description = "Rol táctico en formato de texto", example = "TITULAR")
        private String rolDentroEquipo;

        @Schema(description = "Fecha y hora exactas en las que el jugador se incorporó formalmente", example = "2026-06-03T10:15:30")
        private LocalDateTime fechaIngreso;

        public static MiembroResponse fromEntity(MiembroEquipo m) {
            return MiembroResponse.builder()
                    .id(m.getId())
                    .usuarioId(m.getUsuarioId())
                    .rolDentroEquipo(m.getRolDentroEquipo().name())
                    .fechaIngreso(m.getFechaIngreso())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Modelo de respuesta completo con toda la información y estado operacional de un equipo")
    public static class Response {

        @Schema(description = "ID único incremental del equipo registrado en la base de datos", example = "1")
        private Long id;

        @Schema(description = "Nombre oficial de la escuadra", example = "KRÜ Esports")
        private String nombre;

        @Schema(description = "ID único del capitán asignado", example = "15")
        private Long capitanId;

        @Schema(description = "ID único del videojuego principal asignado", example = "2")
        private Long juegoPrincipalId;

        @Schema(description = "Estado operacional lógico actual del equipo", example = "ACTIVO")
        private String estado;

        @Schema(description = "Regla de negocio calculada: Indica si el equipo cumple los requisitos para torneos", example = "true")
        private boolean puedeInscribirse;

        @Schema(description = "Contador de la cantidad total de miembros en la plantilla", example = "5")
        private int totalMiembros;

        @Schema(description = "Nómina detallada con los jugadores inscritos en este equipo")
        private List<MiembroResponse> miembros;

        @Schema(description = "Fecha de alta del equipo en el sistema", example = "2026-06-01T14:20:00")
        private LocalDateTime fechaCreacion;

        @Schema(description = "Última fecha de modificación de los datos de la escuadra", example = "2026-06-03T10:15:30")
        private LocalDateTime fechaActualizacion;

        public static Response fromEntity(Equipo e) {
            return Response.builder()
                    .id(e.getId())
                    .nombre(e.getNombre())
                    .capitanId(e.getCapitanId())
                    .juegoPrincipalId(e.getJuegoPrincipalId())
                    .estado(e.getEstado().name())
                    .puedeInscribirse(e.getEstado().puedeInscribirse())
                    .totalMiembros(e.getMiembros().size())
                    .miembros(e.getMiembros().stream()
                            .map(MiembroResponse::fromEntity)
                            .toList())
                    .fechaCreacion(e.getFechaCreacion())
                    .fechaActualizacion(e.getFechaActualizacion())
                    .build();
        }
    }
}