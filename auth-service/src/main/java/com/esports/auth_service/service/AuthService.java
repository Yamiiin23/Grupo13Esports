package com.esports.auth_service.service;
import com.esports.auth_service.dto.AuthDTO;
import com.esports.auth_service.exception.AuthException;
import com.esports.auth_service.model.UsuarioAuth;
import com.esports.auth_service.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthDTO.TokenResponse registrar(AuthDTO.RegisterRequest request) {
        log.info("[auth-service] Intentando registrar credenciales para el correo: {}", request.getEmail());

        if (authRepository.existsByEmail(request.getEmail())) {
            log.warn("[auth-service] Registro denegado: el correo electrónico ya existe.");
            throw new AuthException("El email ya se encuentra registrado en el sistema.");
        }

        String passwordEncriptada = passwordEncoder.encode(request.getPassword());

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

        UsuarioAuth usuario = authRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AuthException("Credenciales incorrectas: Usuario no encontrado."));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            log.warn("[auth-service] Contraseña inválida para el usuario: {}", request.getEmail());
            throw new AuthException("Credenciales incorrectas: Contraseña no coincide.");
        }

        log.info("[auth-service] Autenticación exitosa para el correo: {}", usuario.getEmail());
        return generarTokenResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<AuthDTO.TokenResponse> listarCuentas() {
        log.info("[auth-service] Solicitando listado de todas las cuentas de acceso");
        return authRepository.findAll().stream()
                .map(this::generarTokenResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuthDTO.TokenResponse buscarPorId(Long id) {
        log.info("[auth-service] Buscando cuenta de acceso con ID: {}", id);
        UsuarioAuth usuario = authRepository.findById(id)
                .orElseThrow(() -> new AuthException("Cuenta de acceso no encontrada con ID: " + id));
        return generarTokenResponse(usuario);
    }

    public AuthDTO.TokenResponse actualizarRol(Long id, String nuevoRol) {
        log.info("[auth-service] Actualizando rol de la cuenta ID: {} a {}", id, nuevoRol);
        UsuarioAuth usuario = authRepository.findById(id).orElseThrow(() -> new AuthException("Cuenta no encontrada para actualizar."));

        usuario.setRol(nuevoRol.toUpperCase());
        return generarTokenResponse(authRepository.save(usuario));
    }

    public void desactivarCuenta(Long id) {
        log.info("[auth-service] Solicitud para remover/desactivar acceso de cuenta ID: {}", id);
        UsuarioAuth usuario = authRepository.findById(id).orElseThrow(() -> new AuthException("Cuenta no encontrada para eliminar."));

        authRepository.delete(usuario);
        log.info("[auth-service] Acceso eliminado con éxito para la cuenta ID: {}", id);
    }

    private AuthDTO.TokenResponse generarTokenResponse(UsuarioAuth usuario) {
        String tokenSimulado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + Base64.getEncoder().encodeToString(("user:" + usuario.getEmail() + ",role:" + usuario.getRol()).getBytes()) + "." + UUID.randomUUID().toString().replace("-", "");

        return AuthDTO.TokenResponse.builder()
                .token(tokenSimulado)
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .userRefId(usuario.getUserRefId())
                .build();
    }
}
