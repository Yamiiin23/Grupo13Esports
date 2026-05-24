package com.esports.teamservice.dto;

import com.esports.teamservice.model.Equipo;
import com.esports.teamservice.model.MiembroEquipo;
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

public class EquipoDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "El nombre del equipo es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        private String nombre;

        @NotNull(message = "El ID del capitán es obligatorio")
        @Positive(message = "El ID del capitán debe ser un número positivo")
        private Long capitanId;

        @NotNull(message = "El ID del juego principal es obligatorio")
        @Positive(message = "El ID del juego debe ser un número positivo")
        private Long juegoPrincipalId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MiembroRequest {

        @NotNull(message = "El ID del usuario es obligatorio")
        @Positive(message = "El ID del usuario debe ser un número positivo")
        private Long usuarioId;

        private MiembroEquipo.RolEnEquipo rolDentroEquipo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MiembroResponse {
        private Long id;
        private Long usuarioId;
        private String rolDentroEquipo;
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
    public static class Response {
        private Long id;
        private String nombre;
        private Long capitanId;
        private Long juegoPrincipalId;
        private String estado;
        private boolean puedeInscribirse;
        private int totalMiembros;
        private List<MiembroResponse> miembros;
        private LocalDateTime fechaCreacion;
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
