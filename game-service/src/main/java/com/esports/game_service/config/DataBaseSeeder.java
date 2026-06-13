package com.esports.game_service.config;

import com.esports.game_service.model.Juego;
import com.esports.game_service.repository.JuegoRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataBaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataBaseSeeder.class);
    private final JuegoRepository juegoRepository;

    public DataBaseSeeder(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (juegoRepository.count() < 10) {
            log.info("[game-service] Iniciando inyección con DataFaker...");

            Faker faker = new Faker();
            List<Juego> juegosFalsos = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Juego juego = new Juego();

                // Nombre único
                juego.setNombre(faker.esports().game() + " " + faker.number().numberBetween(100, 999));

                // Estos dos campos son obligatorios según tu clase Juego.java
                juego.setGenero("Competitivo");
                juego.setModalidad("5v5");

                // Aquí estaba el error. El nombre correcto de tu variable es este:
                juego.setJugadoresPorEquipo(5);

                juego.setEstado(Juego.EstadoJuego.ACTIVO);

                juegosFalsos.add(juego);
            }

            juegoRepository.saveAll(juegosFalsos);
            log.info("[game-service] ¡Inyección completada! Se guardaron 10 juegos falsos.");
        } else {
            log.info("[game-service] La base de datos ya tiene información. DataFaker omitido.");
        }
    }
}