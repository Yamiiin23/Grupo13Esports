package com.esports.sanctionservice.dto;

import com.esports.sanctionservice.model.Sancion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SancionDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @Positive(message = "El ID del usuario debe ser positivo")
        private Long usuarioId;

        @Positive(message = "El ID del equipo debe ser positivo")
        private Long equipoId;

        @NotBlank(message = "El motivo es obligatorio")
        @Size(min = 5, max = 300, message = "El motivo debe tener entre 5 y 300 caracteres")
        private String motivo;

        @NotNull(message = "La fecha de inicio es obligatoria")
        private LocalDateTime fechaInicio;

        @NotNull(message = "La fecha de fin es obligatoria")
        @Future(message = "La fecha de fin debe ser futura")
        private LocalDateTime fechaFin;

        @NotNull(message = "La severidad es obligatoria")
        private Sancion.SeveridadSancion severidad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadoRequest {

        @NotNull(message = "El estado es obligatorio")
        private Sancion.EstadoSancion estado;

        @Size(max = 300, message = "La justificación no puede superar 300 caracteres")
        private String justificacion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long usuarioId;
        private Long equipoId;
        private String motivo;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFin;
        private String estado;
        private String severidad;
        private boolean bloqueante;
        private boolean vigente;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        public static Response fromEntity(Sancion s) {
            return Response.builder()
                    .id(s.getId())
                    .usuarioId(s.getUsuarioId())
                    .equipoId(s.getEquipoId())
                    .motivo(s.getMotivo())
                    .fechaInicio(s.getFechaInicio())
                    .fechaFin(s.getFechaFin())
                    .estado(s.getEstado().name())
                    .severidad(s.getSeveridad().name())
                    .bloqueante(s.esBloqueante())
                    .vigente(s.estaVigente())
                    .fechaCreacion(s.getFechaCreacion())
                    .fechaActualizacion(s.getFechaActualizacion())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificacionResponse {
        private Long entidadId;
        private String tipoEntidad;
        private boolean bloqueado;
        private String motivo;

        public static VerificacionResponse noBloqueado(Long id, String tipo) {
            return VerificacionResponse.builder()
                    .entidadId(id)
                    .tipoEntidad(tipo)
                    .bloqueado(false)
                    .motivo(null)
                    .build();
        }
        public static VerificacionResponse bloqueado(Long id, String tipo, String motivo) {
            return VerificacionResponse.builder()
                    .entidadId(id)
                    .tipoEntidad(tipo)
                    .bloqueado(true)
                    .motivo(motivo)
                    .build();
        }
    }
}
