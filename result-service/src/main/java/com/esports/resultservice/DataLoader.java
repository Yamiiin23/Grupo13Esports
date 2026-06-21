package com.esports.resultservice;

import com.esports.resultservice.model.Resultado;
import com.esports.resultservice.repository.ResultadoRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ResultadoRepository resultadoRepository;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            Resultado resultado = new Resultado();

            resultado.setPartidaId((long) (i + 1));

            resultado.setGanadorId((long) faker.number().numberBetween(1, 50));

            int puntajeA = faker.number().numberBetween(0, 4);
            int puntajeB = faker.number().numberBetween(0, 4);

            if (puntajeA == puntajeB) {
                puntajeA += 1;
            }

            resultado.setPuntajeA(puntajeA);
            resultado.setPuntajeB(puntajeB);

            Resultado.EstadoValidacion[] estados = Resultado.EstadoValidacion.values();
            resultado.setEstadoValidacion(estados[random.nextInt(estados.length)]);

            resultado.setObservaciones("Partida procesada en el servidor: " + faker.internet().ipV4Address() +
                    ". Latencia media: " + faker.number().numberBetween(15, 60) + "ms.");

            resultadoRepository.save(resultado);
        }
    }
}
