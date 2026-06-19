package com.esports.teamservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ClientDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioResumen {
        private Long id;
        private String nickname;
        private String rol;
        private String estado;
        private boolean puedeCompetar;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JuegoResumen {
        private Long id;
        private String nombre;
        private String modalidad;
        private Integer jugadoresPorEquipo;
        private String estado;
    }
}
