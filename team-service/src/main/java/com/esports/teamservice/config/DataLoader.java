package com.esports.teamservice.config;

import com.esports.teamservice.model.Equipo;
import com.esports.teamservice.model.MiembroEquipo;
import com.esports.teamservice.repository.EquipoRepository;
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

    private final EquipoRepository repository;

    public DataLoader(EquipoRepository repository) {
        this.repository = repository;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API 2026 EQUIPOS")
                .version("1.0")
                .description("Documentación oficial del sistema de Equipos"));
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 8) {
            Faker faker = new Faker();
            for (int i = 0; i < 8; i++) {
                Equipo e = new Equipo();
                e.setNombre(faker.team().name() + " " + faker.number().numberBetween(100, 999));
                e.setCapitanId(1L);
                e.setJuegoPrincipalId(1L);
                e.setEstado(Equipo.EstadoEquipo.ACTIVO);

                MiembroEquipo m = new MiembroEquipo();
                m.setEquipo(e);
                m.setUsuarioId(1L);
                m.setRolDentroEquipo(MiembroEquipo.RolEnEquipo.CAPITAN);
                e.getMiembros().add(m);

                repository.save(e);
            }
        }
    }
}