package com.esports.game_service;


import com.esports.game_service.dto.JuegoDTO;
import com.esports.game_service.exception.JuegoNotFoundException;
import com.esports.game_service.exception.JuegoYaExisteException;
import com.esports.game_service.model.Juego;
import com.esports.game_service.repository.JuegoRepository;
import com.esports.game_service.service.JuegoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JuegoServiceTest {

	@Mock
	private JuegoRepository juegoRepository;

	@InjectMocks
	private JuegoService juegoService;

	private Juego juegoBase;
	private JuegoDTO.Request requestValido;

	@BeforeEach
	void setUp() {
		juegoBase = Juego.builder()
				.id(1L)
				.nombre("Valorant")
				.genero("FPS")
				.modalidad("5v5")
				.jugadoresPorEquipo(5)
				.estado(Juego.EstadoJuego.ACTIVO)
				.fechaCreacion(LocalDateTime.now())
				.fechaActualizacion(LocalDateTime.now())
				.build();

		requestValido = JuegoDTO.Request.builder()
				.nombre("Valorant")
				.genero("FPS")
				.modalidad("5v5")
				.jugadoresPorEquipo(5)
				.build();
	}

	@Test
	@DisplayName("Debe crear un juego cuando el nombre no existe")
	void crearJuego_exitoso() {
		when(juegoRepository.existsByNombreIgnoreCase("Valorant")).thenReturn(false);
		when(juegoRepository.save(any(Juego.class))).thenReturn(juegoBase);

		JuegoDTO.Response result = juegoService.crearJuego(requestValido);

		assertThat(result).isNotNull();
		assertThat(result.getNombre()).isEqualTo("Valorant");
		assertThat(result.getEstado()).isEqualTo("ACTIVO");
		verify(juegoRepository, times(1)).save(any(Juego.class));
	}

	@Test
	@DisplayName("Debe lanzar JuegoYaExisteException si el nombre ya existe")
	void crearJuego_nombreDuplicado_lanzaExcepcion() {
		when(juegoRepository.existsByNombreIgnoreCase("Valorant")).thenReturn(true);

		assertThatThrownBy(() -> juegoService.crearJuego(requestValido))
				.isInstanceOf(JuegoYaExisteException.class)
				.hasMessageContaining("Valorant");

		verify(juegoRepository, never()).save(any());
	}

	@Test
	@DisplayName("Debe retornar el juego cuando existe el ID")
	void buscarPorId_existente_retornaJuego() {
		when(juegoRepository.findById(1L)).thenReturn(Optional.of(juegoBase));

		JuegoDTO.Response result = juegoService.buscarPorId(1L);

		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getNombre()).isEqualTo("Valorant");
	}

	@Test
	@DisplayName("Debe lanzar JuegoNotFoundException cuando el ID no existe")
	void buscarPorId_noExistente_lanzaExcepcion() {
		when(juegoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> juegoService.buscarPorId(99L))
				.isInstanceOf(JuegoNotFoundException.class)
				.hasMessageContaining("99");
	}

	@Test
	@DisplayName("Debe listar solo juegos activos cuando soloActivos=true")
	void listarJuegos_soloActivos() {
		when(juegoRepository.findByEstado(Juego.EstadoJuego.ACTIVO))
				.thenReturn(List.of(juegoBase));

		List<JuegoDTO.Response> result = juegoService.listarJuegos(true);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getEstado()).isEqualTo("ACTIVO");
		verify(juegoRepository).findByEstado(Juego.EstadoJuego.ACTIVO);
		verify(juegoRepository, never()).findAll();
	}

	@Test
	@DisplayName("Debe desactivar el juego correctamente")
	void desactivarJuego_exitoso() {
		when(juegoRepository.findById(1L)).thenReturn(Optional.of(juegoBase));
		when(juegoRepository.save(any(Juego.class))).thenAnswer(inv -> inv.getArgument(0));

		JuegoDTO.Response result = juegoService.desactivarJuego(1L);

		assertThat(result.getEstado()).isEqualTo("INACTIVO");
		verify(juegoRepository).save(argThat(j -> j.getEstado() == Juego.EstadoJuego.INACTIVO));
	}

	@Test
	@DisplayName("Debe lanzar excepción al desactivar ID inexistente")
	void desactivarJuego_noExistente_lanzaExcepcion() {
		when(juegoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> juegoService.desactivarJuego(99L))
				.isInstanceOf(JuegoNotFoundException.class);
	}
}
