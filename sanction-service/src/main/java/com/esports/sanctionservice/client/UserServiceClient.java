package com.esports.sanctionservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "user-service", url = "${feign.client.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/api/v1/usuarios/{id}/resumen")
    UsuarioResumen obtenerResumenUsuario(@PathVariable("id") Long id);

    @Data @NoArgsConstructor @AllArgsConstructor
    class UsuarioResumen {
        private Long id;
        private String nickname;
        private String rol;
        private String estado;
        private boolean puedeCompetar;
    }
}
