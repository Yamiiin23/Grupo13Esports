package com.esports.resultservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI cutomOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API 2026 RESULTADOS")
                        .version("1.0")
                        .description("Documentacion de la API para el sistema de Resultados"));
    }
}
