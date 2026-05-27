package com.esports.auth_service.service;


import com.esports.auth_service.dto.AuthDTO;
import com.esports.auth_service.exception.AuthException;
import com.esports.auth_service.model.UsuarioAuth;
import com.esports.auth_service.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public AuthDTO.TokenResponse registrar(AuthDTO.RegisterRequest request) {
        log.info("[auth-service] Intentando registrar credenciales para el correo: {}", request.getEmail());

        if (authRepository.existsByEmail(request.getEmail())) {
            log.warn("[auth-service] Registro denegado: el correo electrónico ya existe.");
            throw new AuthException("El email ya se encuentra registrado en el sistema.");
        }

        String passwordEncriptada = Base64.getEncoder().encodeToString(request.getPassword().getBytes());

        UsuarioAuth nuevoUsuario = UsuarioAuth.builder()
                .email(request.getEmail())
                .password(passwordEncriptada)
                .rol(request.getRol() != null ? request.getRol().toUpperCase() : "JUGADOR")
                .userRefId(request.getUserRefId())
                .build();

        authRepository.save(nuevoUsuario);
        log.info("[auth-service] Credenciales registradas exitosamente para ID de Perfil={}", nuevoUsuario.getUserRefId());

        return generarTokenResponse(nuevoUsuario);
    }

    @Transactional(readOnly = true)
    public AuthDTO.TokenResponse login(AuthDTO.LoginRequest request) {
        log.info("[auth-service] Intento de login iniciado para: {}", request.getEmail());

        UsuarioAuth usuario = authRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Credenciales incorrectas: Usuario no encontrado."));

        String passwordEntranteEncriptada = Base64.getEncoder().encodeToString(request.getPassword().getBytes());

        if (!usuario.getPassword().equals(passwordEntranteEncriptada)) {
            log.warn("[auth-service] Contraseña inválida para el usuario: {}", request.getEmail());
            throw new AuthException("Credenciales incorrectas: Contraseña no coincide.");
        }

        log.info("[auth-service] Autenticación exitosa para el correo: {}", usuario.getEmail());
        return generarTokenResponse(usuario);
    }

    private AuthDTO.TokenResponse generarTokenResponse(UsuarioAuth usuario) {
        String tokenSimulado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                Base64.getEncoder().encodeToString(("user:" + usuario.getEmail() + ",role:" + usuario.getRol()).getBytes()) +
                "." + UUID.randomUUID().toString().replace("-", "");

        return AuthDTO.TokenResponse.builder()
                .token(tokenSimulado)
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .userRefId(usuario.getUserRefId())
                .build();
    }
}