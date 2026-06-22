package com.esports.registration_service.config;

import com.esports.registration_service.model.Inscripcion;
import com.esports.registration_service.repository.InscripcionRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final InscripcionRepository repository;

    public DataLoader(InscripcionRepository repository) {
        this.repository = repository;
    }

    // Cabecera de OpenAPI (Sustituye a tu antiguo SwaggerConfig)
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API 2026 INSCRIPCIONES")
                .version("1.0")
                .description("Documentación oficial del sistema de Inscripciones"));
    }

    // Generador de datos (DataFaker)
    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 20) {
            Faker faker = new Faker();
            for (int i = 0; i < 20; i++) {
                Inscripcion inscripcion = new Inscripcion();

                // Simula inscripciones para torneos (del 1 al 10) y equipos (del 1 al 20)
                inscripcion.setTorneoId((long) faker.number().numberBetween(1, 10));
                inscripcion.setEquipoId((long) faker.number().numberBetween(1, 20));

                // Elige un estado al azar
                Inscripcion.EstadoInscripcion estado = faker.options().option(Inscripcion.EstadoInscripcion.class);
                inscripcion.setEstado(estado);

                // Si está cancelada o rechazada, le agregamos un motivo falso
                if (estado == Inscripcion.EstadoInscripcion.CANCELADA || estado == Inscripcion.EstadoInscripcion.RECHAZADA) {
                    inscripcion.setMotivoCancelacion(faker.lorem().sentence());
                }

                repository.save(inscripcion);
            }
        }
    }
}