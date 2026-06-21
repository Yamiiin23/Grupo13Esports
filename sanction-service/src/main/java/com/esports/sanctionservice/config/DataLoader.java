package com.esports.sanctionservice.config;

import com.esports.sanctionservice.model.Sancion;
import com.esports.sanctionservice.repository.SancionRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private SancionRepository sancionRepository;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

        for (int i = 0; i < 25; i++) {
            Sancion sancion = new Sancion();

            if (random.nextBoolean()) {
                sancion.setUsuarioId((long) faker.number().numberBetween(1, 150));
                sancion.setEquipoId(null);
            } else {
                sancion.setUsuarioId(null);
                sancion.setEquipoId((long) faker.number().numberBetween(1, 30));
            }

            String motivoIncidencia = "Infracción en el torneo: " + faker.esports().event() +
                    ". Detalle: " + faker.lorem().sentence(4);
            sancion.setMotivo(motivoIncidencia);

            LocalDateTime fechaInicio = LocalDateTime.now().minusDays(random.nextInt(4));
            LocalDateTime fechaFin = LocalDateTime.now().plusDays(random.nextInt(12) + 2);

            sancion.setFechaInicio(fechaInicio);
            sancion.setFechaFin(fechaFin);

            Sancion.SeveridadSancion[] severidades = Sancion.SeveridadSancion.values();
            sancion.setSeveridad(severidades[random.nextInt(severidades.length)]);

            sancion.setEstado(Sancion.EstadoSancion.ACTIVA);

            sancionRepository.save(sancion);
        }
    }
}
