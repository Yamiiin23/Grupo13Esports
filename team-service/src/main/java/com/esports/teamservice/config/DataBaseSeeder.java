package com.esports.teamservice.config;

import com.esports.teamservice.model.Equipo;
import com.esports.teamservice.model.MiembroEquipo;
import com.esports.teamservice.repository.EquipoRepository;
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
    private final EquipoRepository equipoRepository;

    public DataBaseSeeder(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Solo inyectamos si la tabla tiene menos de 8 registros para evitar duplicados
        if (equipoRepository.count() < 8) {
            log.info("[team-service] Iniciando inyección automática de equipos con DataFaker...");

            Faker faker = new Faker();
            List<Equipo> equiposFalsos = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                Equipo equipo = new Equipo();

                // Nombres de equipos únicos para no violar la restricción unique = true
                equipo.setNombre(faker.team().name() + " " + faker.number().numberBetween(100, 999));

                // Generamos identificadores lógicos aleatorios para enlazar con usuarios y juegos
                Long capitanId = faker.number().numberBetween(1L, 10L);
                Long juegoId = faker.number().numberBetween(1L, 10L);

                equipo.setCapitanId(capitanId);
                equipo.setJuegoPrincipalId(juegoId);
                equipo.setEstado(Equipo.EstadoEquipo.ACTIVO);

                // Instanciamos el MiembroEquipo correspondiente para el capitán del equipo
                MiembroEquipo miembroCapitan = new MiembroEquipo();
                miembroCapitan.setEquipo(equipo);
                miembroCapitan.setUsuarioId(capitanId);
                miembroCapitan.setRolDentroEquipo(MiembroEquipo.RolEnEquipo.CAPITAN);

                // Agregamos el miembro a la lista interna gestionada por cascada en la entidad Equipo
                equipo.getMiembros().add(miembroCapitan);

                equiposFalsos.add(equipo);
            }

            // Al guardar la lista de equipos se guardan sus miembros asociados por el CascadeType.ALL
            equipoRepository.saveAll(equiposFalsos);
            log.info("[team-service] ¡Inyección completada! Se registraron 8 equipos con sus respectivos capitanes.");
        } else {
            log.info("[team-service] Base de datos de equipos con registros previos. Omitiendo DataFaker.");
        }
    }
}