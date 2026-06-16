package com.esports.auth_service.service;
import com.esports.auth_service.dto.AuthDTO;
import com.esports.auth_service.exception.AuthException;
import com.esports.auth_service.model.UsuarioAuth;
import com.esports.auth_service.repository.AuthRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public AuthDTO.TokenResponse registrar(AuthDTO.RegisterRequest request) {
        if (authRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("El email ya se encuentra registrado en el sistema.");
        }
        String passwordEncriptada = passwordEncoder.encode(request.getPassword());
        UsuarioAuth nuevoUsuario = UsuarioAuth.builder()
                .email(request.getEmail())
                .password(passwordEncriptada)
                .rol(request.getRol())
                .userRefId(request.getUserRefId())
                .build();
        UsuarioAuth guardado = authRepository.save(nuevoUsuario);
        String tokenSimulado = "eyJhbGciOiJIUzI1NiJ9.SimuladoUser" + guardado.getId();
        return convertirATokenResponse(guardado, tokenSimulado);
    }

    @Transactional(readOnly = true)
    public AuthDTO.TokenResponse login(AuthDTO.LoginRequest request) {
        UsuarioAuth usuario = authRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AuthException("Credenciales incorrectas: Email no encontrado."));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new AuthException("Credenciales incorrectas: Contraseña no coincide.");
        }

        String tokenSimulado = "eyJhbGciOiJIUzI1NiJ9.SimuladoUser" + usuario.getId();
        return convertirATokenResponse(usuario, tokenSimulado);
    }

    @Transactional(readOnly = true)
    public List<AuthDTO.TokenResponse> listarCuentas() {
        return authRepository.findAll().stream()
                .map(usuario -> convertirATokenResponse(usuario, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuthDTO.TokenResponse buscarPorId(Long id) {
        UsuarioAuth usuario = authRepository.findById(id)
                .orElseThrow(() -> new AuthException("Cuenta de acceso no encontrada con ID: " + id));
        return convertirATokenResponse(usuario, null);
    }

    @Transactional
    public AuthDTO.TokenResponse actualizarRol(Long id, String nuevoRol) {
        UsuarioAuth usuario = authRepository.findById(id)
                .orElseThrow(() -> new AuthException("Cuenta no encontrada para actualizar."));
        usuario.setRol(nuevoRol);
        return convertirATokenResponse(authRepository.save(usuario), null);
    }

    @Transactional
    public void eliminarOAnularCuenta(Long id) {
        UsuarioAuth usuario = authRepository.findById(id)
                .orElseThrow(() -> new AuthException("Cuenta no encontrada para eliminar."));
        authRepository.delete(usuario);
    }

    private AuthDTO.TokenResponse convertirATokenResponse(UsuarioAuth usuario, String token) {
        return AuthDTO.TokenResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .userRefId(usuario.getUserRefId())
                .build();
    }
}