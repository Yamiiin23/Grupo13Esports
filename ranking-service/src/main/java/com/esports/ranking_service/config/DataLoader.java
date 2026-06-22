package com.esports.ranking_service.config;

import com.esports.ranking_service.model.Ranking;
import com.esports.ranking_service.repository.RankingRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final RankingRepository repository;

    public DataLoader(RankingRepository repository) {
        this.repository = repository;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API 2026 RANKING")
                .version("1.0")
                .description("Documentación oficial del sistema de Rankings"));
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 20) {
            Faker faker = new Faker();
            for (int i = 0; i < 20; i++) {
                Ranking ranking = new Ranking();

                // Simular IDs de torneos y participantes
                ranking.setTorneoId((long) faker.number().numberBetween(1, 10));
                ranking.setParticipanteId((long) faker.number().numberBetween(1, 50));

                // Generar estadísticas ficticias (Victorias, Derrotas y Puntos)
                int victorias = faker.number().numberBetween(0, 15);
                int derrotas = faker.number().numberBetween(0, 10);

                ranking.setVictorias(victorias);
                ranking.setDerrotas(derrotas);
                ranking.setPuntos(victorias * 3); // 3 puntos por victoria
                ranking.setDiferencia(victorias - derrotas);
                ranking.setPosicion(faker.number().numberBetween(1, 20));

                repository.save(ranking);
            }
        }
    }
}