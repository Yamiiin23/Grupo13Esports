package com.esports.tournament_service.config;

import com.esports.tournament_service.model.Torneo;
import com.esports.tournament_service.repository.TorneoRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final TorneoRepository repository;

    public DataLoader(TorneoRepository repository) {
        this.repository = repository;
    }

    // Configuración de la cabecera de Swagger/OpenAPI
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API 2026 TORNEOS")
                .version("1.0")
                .description("Documentación oficial del sistema de Torneos"));
    }

    // Generador de datos automáticos (DataFaker)
    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 20) {
            Faker faker = new Faker();
            for (int i = 0; i < 20; i++) {
                Torneo t = new Torneo();

                // Genera nombres como "Worlds 2026", "ESL Pro League 105", etc.
                t.setNombre(faker.esports().event() + " " + faker.number().numberBetween(1, 100));

                // Asignamos un ID de juego ficticio entre 1 y 10 (simulando juegos que existen)
                t.setGameId((long) faker.number().numberBetween(1, 10));

                // Asignamos estados y participantes
                t.setEstado(Torneo.EstadoTorneo.INSCRIPCION);
                t.setMaxParticipantes(faker.options().option(16, 32, 64));
                t.setParticipantesActuales(faker.number().numberBetween(0, 10));

                repository.save(t);
            }
        }
    }
}