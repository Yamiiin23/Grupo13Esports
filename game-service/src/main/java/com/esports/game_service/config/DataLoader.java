package com.esports.game_service.config;

import com.esports.game_service.model.Juego;
import com.esports.game_service.repository.JuegoRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private final JuegoRepository repository;

    public DataLoader(JuegoRepository repository) {
        this.repository = repository;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API 2026 JUEGOS")
                .version("1.0")
                .description("Documentación oficial del sistema de Juegos"));
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 10) {
            Faker faker = new Faker();
            for (int i = 0; i < 10; i++) {
                Juego j = new Juego();
                j.setNombre(faker.esports().game() + " " + faker.number().numberBetween(100, 999));
                j.setGenero("Competitivo");
                j.setModalidad("5v5");
                j.setJugadoresPorEquipo(5);
                j.setEstado(Juego.EstadoJuego.ACTIVO);
                repository.save(j);
            }
        }
    }
}