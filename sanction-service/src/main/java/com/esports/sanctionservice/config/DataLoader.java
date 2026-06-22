package com.esports.sanctionservice.config;

import com.esports.sanctionservice.model.Sancion;
import com.esports.sanctionservice.repository.SancionRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final SancionRepository repository;

    public DataLoader(SancionRepository repository) {
        this.repository = repository;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                        .title("API 2026 SANCION")
                        .version("1.0")
                        .description("Documentacion de la API para el sistema de Sancion"));
    }
    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 20) {
            Faker faker = new Faker();

            // Lista de motivos realistas para un entorno competitivo de Esports
            List<String> motivos = List.of(
                    "Uso indebido de software de terceros (Aimlock/Wallhack detectado)",
                    "Toxicidad verbal extrema y acoso en los chats de emparejamiento globales",
                    "Incomparecencia injustificada (No-Show) a los partidos oficiales de la liga",
                    "Sospecha legítima de manipulación de resultados (Match-fixing)",
                    "Suplantación de identidad en torneo presencial (Account Sharing)",
                    "Abuso reiterado de mecánicas de juego rotas (Exploit de Bugs bajo advertencia)",
                    "Comportamiento antideportivo grave durante la fase de picks y bans"
            );

            for (int i = 0; i < 20; i++) {
                String motivoAleatorio = motivos.get(faker.number().numberBetween(0, motivos.size()));

                // Distribución temporal: Fechas de inicio y fin coherentes
                LocalDateTime fechaInicio = LocalDateTime.now().minusDays(faker.number().numberBetween(1, 15));
                LocalDateTime fechaFin = fechaInicio.plusDays(faker.number().numberBetween(3, 30));

                // Selección aleatoria de Enums propios de la Entidad
                Sancion.EstadoSancion[] estados = Sancion.EstadoSancion.values();
                Sancion.EstadoSancion estadoAleatorio = estados[faker.number().numberBetween(0, estados.length)];

                Sancion.SeveridadSancion[] severidades = Sancion.SeveridadSancion.values();
                Sancion.SeveridadSancion severidadAleatoria = severidades[faker.number().numberBetween(0, severidades.length)];

                Sancion.SancionBuilder sancionBuilder = Sancion.builder()
                        .motivo(motivoAleatorio)
                        .fechaInicio(fechaInicio)
                        .fechaFin(fechaFin)
                        .estado(estadoAleatorio)
                        .severidad(severidadAleatoria);

                // El 50% de las veces se sanciona a un usuario y el otro 50% a un equipo completo
                if (faker.bool().bool()) {
                    sancionBuilder.usuarioId(faker.number().numberBetween(1L, 50L));
                    sancionBuilder.equipoId(null);
                } else {
                    sancionBuilder.usuarioId(null);
                    sancionBuilder.equipoId(faker.number().numberBetween(1L, 20L));
                }

                repository.save(sancionBuilder.build());
            }
            System.out.println("¡Éxito! Se han inyectado 20 registros iniciales de sanciones en H2.");
        }
    }
}

