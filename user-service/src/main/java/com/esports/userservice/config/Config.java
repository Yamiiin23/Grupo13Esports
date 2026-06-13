package com.esports.userservice.config;

import com.esports.userservice.model.Usuario;
import com.esports.userservice.repository.UsuarioRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class Config implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private final UsuarioRepository usuarioRepository;

    // Inyectamos el repositorio para poder guardar en la BD
    public Config(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Solo inyectamos datos si la tabla de usuarios está completamente vacía
        if (usuarioRepository.count() < 20) {
            log.info("[user-service] Base de datos vacía. Iniciando inyección con DataFaker...");

            Faker faker = new Faker();
            List<Usuario> usuariosFalsos = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                Usuario usuario = new Usuario();
                usuario.setNombre(faker.name().fullName());

                // Solución al error Duplicate Entry:
                usuario.setNickname(faker.esports().player() + faker.number().numberBetween(100, 999));
                usuario.setEmail(faker.number().numberBetween(100, 999) + faker.internet().emailAddress());

                usuario.setRol(Usuario.RolUsuario.JUGADOR);
                usuario.setEstado(Usuario.EstadoUsuario.ACTIVO);

                usuariosFalsos.add(usuario);
            }

            usuarioRepository.saveAll(usuariosFalsos);
            log.info("[user-service] ¡Inyección completada! Se guardaron 20 usuarios falsos.");
        } else {
            log.info("[user-service] La base de datos ya tiene información. DataFaker omitido.");
        }
    }
}