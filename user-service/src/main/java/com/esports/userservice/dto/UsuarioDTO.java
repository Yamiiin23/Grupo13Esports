package com.esports.userservice.dto;

import com.esports.userservice.model.Usuario;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UsuarioDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        private String nombre;

        @NotBlank(message = "El nickname es obligatorio")
        @Size(min = 3, max = 60, message = "El nickname debe tener entre 3 y 60 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$",
                 message = "El nickname solo puede contener letras, números, guiones y guiones bajos")
        private String nickname;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 120, message = "El email no puede superar 120 caracteres")
        private String email;

        @NotNull(message = "El rol es obligatorio")
        private Usuario.RolUsuario rol;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadoRequest {

        @NotNull(message = "El estado es obligatorio")
        private Usuario.EstadoUsuario estado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String nombre;
        private String nickname;
        private String email;
        private String rol;
        private String estado;
        private boolean puedeCompetar;
        private LocalDateTime fechaRegistro;
        private LocalDateTime fechaActualizacion;

        public static Response fromEntity(Usuario u) {
            return Response.builder()
                    .id(u.getId())
                    .nombre(u.getNombre())
                    .nickname(u.getNickname())
                    .email(u.getEmail())
                    .rol(u.getRol().name())
                    .estado(u.getEstado().name())
                    .puedeCompetar(u.getEstado().puedeCompetar())
                    .fechaRegistro(u.getFechaRegistro())
                    .fechaActualizacion(u.getFechaActualizacion())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenResponse {
        private Long id;
        private String nickname;
        private String rol;
        private String estado;
        private boolean puedeCompetar;

        public static ResumenResponse fromEntity(Usuario u) {
            return ResumenResponse.builder()
                    .id(u.getId())
                    .nickname(u.getNickname())
                    .rol(u.getRol().name())
                    .estado(u.getEstado().name())
                    .puedeCompetar(u.getEstado().puedeCompetar())
                    .build();
        }
    }
}
