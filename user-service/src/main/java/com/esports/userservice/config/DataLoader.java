package com.esports.userservice.config;

import com.esports.userservice.model.Usuario;
import com.esports.userservice.repository.UsuarioRepository;
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

    private final UsuarioRepository repository;

    public DataLoader(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API 2026 USUARIOS")
                .version("1.0")
                .description("Documentación oficial del sistema de Usuarios"));
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 20) {
            Faker faker = new Faker();
            for (int i = 0; i < 20; i++) {
                Usuario u = new Usuario();
                u.setNombre(faker.name().fullName());
                u.setNickname(faker.esports().player() + faker.number().numberBetween(100, 999));
                u.setEmail(faker.internet().emailAddress());
                u.setRol(Usuario.RolUsuario.JUGADOR);
                u.setEstado(Usuario.EstadoUsuario.ACTIVO);
                repository.save(u);
            }
        }
    }
}