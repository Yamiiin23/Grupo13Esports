package com.esports.auth_service;

import com.esports.auth_service.dto.AuthDTO;
import com.esports.auth_service.exception.AuthException;
import com.esports.auth_service.model.UsuarioAuth;
import com.esports.auth_service.repository.AuthRepository;
import com.esports.auth_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private AuthRepository authRepository;

	@InjectMocks
	private AuthService authService;

	@Test
	void registrar_DebeGuardarCredencialesYDevolverToken_CuandoEmailNoExiste() {
		AuthDTO.RegisterRequest request = AuthDTO.RegisterRequest.builder()
				.email("proplayer@esports.com")
				.password("password123")
				.rol("JUGADOR")
				.userRefId(1L)
				.build();

		when(authRepository.existsByEmail(request.getEmail())).thenReturn(false);

		when(authRepository.save(any(UsuarioAuth.class))).thenAnswer(invocation -> invocation.getArgument(0));

		AuthDTO.TokenResponse resultado = authService.registrar(request);

		assertNotNull(resultado);
		assertNotNull(resultado.getToken());
		assertEquals("proplayer@esports.com", resultado.getEmail());
		assertEquals("JUGADOR", resultado.getRol());
		assertEquals(1L, resultado.getUserRefId());

		verify(authRepository, times(1)).existsByEmail(request.getEmail());
		verify(authRepository, times(1)).save(any(UsuarioAuth.class));
	}

	@Test
	void registrar_DebeLanzarExcepcion_CuandoEmailYaExiste() {
		AuthDTO.RegisterRequest request = AuthDTO.RegisterRequest.builder()
				.email("duplicado@esports.com")
				.password("secret123")
				.userRefId(2L)
				.build();

		when(authRepository.existsByEmail(request.getEmail())).thenReturn(true);

		AuthException excepcion = assertThrows(AuthException.class, () -> {
			authService.registrar(request);
		});

		assertEquals("El email ya se encuentra registrado en el sistema.", excepcion.getMessage());

		verify(authRepository, never()).save(any(UsuarioAuth.class));
	}

	@Test
	void login_DebeDevolverToken_CuandoCredencialesSonCorrectas() {
		AuthDTO.LoginRequest request = AuthDTO.LoginRequest.builder()
				.email("admin@esports.com")
				.password("adminPass")
				.build();

		String passwordEncriptada = Base64.getEncoder().encodeToString("adminPass".getBytes());

		UsuarioAuth usuarioExistente = UsuarioAuth.builder()
				.id(10L)
				.email("admin@esports.com")
				.password(passwordEncriptada)
				.rol("ADMIN")
				.userRefId(99L)
				.build();

		when(authRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuarioExistente));

		AuthDTO.TokenResponse resultado = authService.login(request);

		assertNotNull(resultado);
		assertNotNull(resultado.getToken());
		assertEquals("ADMIN", resultado.getRol());
		assertEquals(99L, resultado.getUserRefId());
		verify(authRepository, times(1)).findByEmail(request.getEmail());
	}

	@Test
	void login_DebeLanzarExcepcion_CuandoContrasenaEsIncorrecta() {
		AuthDTO.LoginRequest request = AuthDTO.LoginRequest.builder()
				.email("user@esports.com")
				.password("clave_incorrecta")
				.build();

		String passwordRealEncriptada = Base64.getEncoder().encodeToString("clave_correcta".getBytes());

		UsuarioAuth usuarioExistente = UsuarioAuth.builder()
				.email("user@esports.com")
				.password(passwordRealEncriptada)
				.rol("JUGADOR")
				.build();

		when(authRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuarioExistente));

		AuthException excepcion = assertThrows(AuthException.class, () -> {
			authService.login(request);
		});

		assertEquals("Credenciales incorrectas: Contraseña no coincide.", excepcion.getMessage());
	}
}
