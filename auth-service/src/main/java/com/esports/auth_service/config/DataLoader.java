package com.esports.auth_service.config;

import com.esports.auth_service.model.UsuarioAuth;
import com.esports.auth_service.repository.AuthRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
//@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private final AuthRepository repository;

    public DataLoader(AuthRepository repository) {
        this.repository = repository;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API 2026 AUTENTICACIÓN")
                .version("1.0")
                .description("Documentación oficial del sistema de Autenticación y Seguridad"));
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 20) {
            Faker faker = new Faker();
            for (int i = 0; i < 20; i++) {
                UsuarioAuth auth = new UsuarioAuth();

                // Usamos los campos reales de tu clase UsuarioAuth
                auth.setEmail(faker.internet().emailAddress());
                auth.setPassword("clave123");
                auth.setRol("JUGADOR");

                // Simulamos que este auth pertenece a un usuario (ID del 1 al 20)
                auth.setUserRefId((long) faker.number().numberBetween(1, 21));

                repository.save(auth);
            }
            System.out.println("[auth-service] Datos falsos de Autenticación cargados exitosamente.");
        }
    }
}