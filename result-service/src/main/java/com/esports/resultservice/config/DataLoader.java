package com.esports.resultservice.config;

import com.esports.resultservice.model.Resultado;
import com.esports.resultservice.repository.ResultadoRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final ResultadoRepository repository;

    public DataLoader(ResultadoRepository repository) {
        this.repository = repository;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API 2026 RESULTADOS")
                .version("1.0")
                .description("Documentación oficial del sistema de Resultados"));
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 20) {
            Faker faker = new Faker();

            for (int i = 0; i < 20; i++) {
                // Simulamos puntajes competitivos de Esports (ej. rondas o mapas de 0 a 16)
                int scoreA = faker.number().numberBetween(0, 16);
                int scoreB = faker.number().numberBetween(0, 16);
                if (scoreA == scoreB) {
                    scoreA++; // Evitamos empates directos para definir un ganador claro
                }

                Long partidaId = (long) (i + 1);
                Long participanteA = faker.number().numberBetween(1L, 30L);
                Long participanteB = faker.number().numberBetween(31L, 60L);

                // El ganador lógico según los puntajes generados
                Long ganadorId = (scoreA > scoreB) ? participanteA : participanteB;

                Resultado.EstadoValidacion[] estados = Resultado.EstadoValidacion.values();
                Resultado.EstadoValidacion estadoAleatorio = estados[faker.number().numberBetween(0, estados.length)];

                Resultado resultado = Resultado.builder()
                        .partidaId(partidaId)
                        .ganadorId(ganadorId)
                        .puntajeA(scoreA)
                        .puntajeB(scoreB)
                        .estadoValidacion(estadoAleatorio)
                        .observaciones("Partida simulada: " + faker.esports().event())
                        .build();

                repository.save(resultado);
            }
            System.out.println("¡Éxito! Se han inyectado 20 resultados base en la consola de H2.");
        }
    }
}