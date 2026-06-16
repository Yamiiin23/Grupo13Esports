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
	void login_DebeDevolverToken_CuandoCredencialesSonCorrectas() {
		AuthDTO.LoginRequest request = AuthDTO.LoginRequest.builder()
				.email("admin@esports.com")
				.password("adminPass")
				.build();

		String passwordEncriptadaBCrypt = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("adminPass");

		UsuarioAuth usuarioExistente = UsuarioAuth.builder()
				.id(10L)
				.email("admin@esports.com")
				.password(passwordEncriptadaBCrypt)
				.rol("ADMIN")
				.userRefId(99L)
				.build();

		when(authRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuarioExistente));

		AuthDTO.TokenResponse resultado = authService.login(request);

		assertNotNull(resultado);
		assertEquals("ADMIN", resultado.getRol());
		verify(authRepository, times(1)).findByEmail(request.getEmail());
	}
}