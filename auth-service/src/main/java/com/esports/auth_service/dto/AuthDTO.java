package com.esports.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

public class AuthDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    //swagger
    @Schema(description ="Modelo requerido para el registro de un nuevo usuario")
    public static class RegisterRequest {
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        @Schema(description = "Correo electrónico único de acceso", example = "jugador1@esports.cl")

        private String email;
        // swagger
        @NotBlank(message = "la contraseña es obligatorio")
        @Size(min = 6, message= "La contraseña debe tener al menos 6 carácteres")
        @Schema(description = "Contraseña  de acceso (minimo 6 caracteres)", example = "password123")
        private String password;

        //cambio guia 1
        @Schema(description = "Rol asignado en la plataforma", example = "PLAYER", allowableValues = {"ADMIN", "PLAYER", "ORGANIZER"})
        private String rol;

        @NotNull(message = "El ID de referencia del perfil de usuario es obligatorio")
        //otro cambio
        @Schema(description = "ID vinculado al perfil detallado del usuario en user-service", example = "1")
        private Long userRefId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Modelo requerido para iniciar sesión")

    public static class LoginRequest {
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email incorrecto")
        @Schema(description = "Correo electrónico registrado", example = "jugador1@esports.cl")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Schema(description = "Contraseña correspondiente a la cuenta", example = "password123")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Respuesta devuelta tras un login o registro exitoso")
    public static class TokenResponse {
        @Schema(description = "Token JWT de tipo Bearer necesario para consumir endpoints protegidos", example = "eyJhbGciOiJIUzI1NiJ9...")
        private String token;

        @Schema(description = "Email del usuario autenticado", example = "jugador1@esports.cl")
        private String email;

        @Schema(description = "Rol del usuario autenticado", example = "PLAYER")
        private String rol;

        @Schema(description = "ID de referencia del perfil", example = "1")
        private Long userRefId;
    }
}
