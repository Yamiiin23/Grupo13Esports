package com.esports.tournament_service;

import com.esports.tournament_service.controller.TorneoController;
import com.esports.tournament_service.dto.TorneoDTO;
import com.esports.tournament_service.exception.TorneoNotFoundException;
import com.esports.tournament_service.exception.TorneoYaExisteException;
import com.esports.tournament_service.model.Torneo;
import com.esports.tournament_service.repository.TorneoRepository;
import com.esports.tournament_service.service.TorneoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TorneoServiceTest {

	@Mock
	private TorneoRepository torneoRepository;

	@InjectMocks
	private TorneoService torneoService;

	private Torneo torneoMock;
	private TorneoDTO.Request torneoRequest;

	@BeforeEach
	void setUp() {
		torneoMock = Torneo.builder()
				.id(1L)
				.nombre("Copa de Campeones")
				.gameId(99L)
				.fechaCreacion(LocalDateTime.now())
				.build();
		torneoRequest = TorneoDTO.Request.builder()
				.nombre("Copa de Campeones")
				.gameId(99L)
				.maxParticipantes(8)
				.build();
	}

	@Test
	@DisplayName("Debería crear un torneo exitosamente cuando el nombre no está duplicado")
	void crearTorneoExitoso() {
		when(torneoRepository.existsByNombreIgnoreCase(torneoRequest.getNombre())).thenReturn(false);
		when(torneoRepository.save(any(Torneo.class))).thenReturn(torneoMock);
		TorneoDTO.Response response = torneoService.crearTorneo(torneoRequest);
		assertNotNull(response);
		assertEquals(1L, response.getId());
		assertEquals("Copa de Campeones", response.getNombre());
		assertEquals(99L, response.getGameId());
		verify(torneoRepository, times(1)).save(any(Torneo.class));
	}

	@Test
	@DisplayName("Debería lanzar TorneoYaExisteException al crear si el nombre ya existe")
	void crearTorneoNombreDuplicado() {
		when(torneoRepository.existsByNombreIgnoreCase(torneoRequest.getNombre())).thenReturn(true);
		assertThrows(TorneoYaExisteException.class, () -> {
			torneoService.crearTorneo(torneoRequest);
		});
		verify(torneoRepository, never()).save(any(Torneo.class));
	}

	@Test
	@DisplayName("Debería retornar una lista con todos los torneos transformados a DTO")
	void listarTorneosExitoso() {
		when(torneoRepository.findAll()).thenReturn(List.of(torneoMock));
		List<TorneoDTO.Response> resultado = torneoService.listarTorneos();
		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		assertEquals("Copa de Campeones", resultado.getFirst().getNombre());
	}

	@Test
	@DisplayName("Debería encontrar y retornar un torneo por su ID")
	void buscarPorIdExitoso() {
		when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoMock));
		TorneoDTO.Response response = torneoService.buscarPorId(1L);
		assertNotNull(response);
		assertEquals(1L, response.getId());
	}

	@Test
	@DisplayName("Debería lanzar TorneoNotFoundException si el ID no existe")
	void buscarPorIdNoEncontrado() {
		when(torneoRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(TorneoNotFoundException.class, () -> {
			torneoService.buscarPorId(1L);
		});
	}

	@Test
	@DisplayName("Debería actualizar los datos del torneo correctamente si no hay conflicto de nombres")
	void actualizarTorneoExitoso() {
		torneoMock.setEstado(Torneo.EstadoTorneo.INSCRIPCION);
		TorneoDTO.Request nuevoRequest = TorneoDTO.Request.builder()
				.nombre("Liga de Verano")
				.gameId(88L)
				.build();
		when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoMock));
		when(torneoRepository.existsByNombreIgnoreCase("Liga de Verano")).thenReturn(false);
		when(torneoRepository.save(any(Torneo.class))).thenAnswer(invocation -> invocation.getArgument(0));
		TorneoDTO.Response response = torneoService.actualizarTorneo(1L, nuevoRequest);
		assertNotNull(response);
		assertEquals("Liga de Verano", response.getNombre());
		assertEquals(88L, response.getGameId());
	}

	@Test
	@DisplayName("Debería lanzar TorneoYaExisteException al actualizar si el nuevo nombre ya está en uso por otro torneo")
	void actualizarTorneoNombreEnUso() {
		torneoMock.setEstado(Torneo.EstadoTorneo.INSCRIPCION);
		TorneoDTO.Request nuevoRequest = TorneoDTO.Request.builder()
				.nombre("Torneo Existente")
				.gameId(99L)
				.build();
		when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoMock));
		when(torneoRepository.existsByNombreIgnoreCase("Torneo Existente")).thenReturn(true);
		assertThrows(TorneoYaExisteException.class, () -> {
			torneoService.actualizarTorneo(1L, nuevoRequest);
		});
		verify(torneoRepository, never()).save(any(Torneo.class));
	}

	@Test
	@DisplayName("Debería eliminar lógicamente el torneo")
	void eliminarTorneoExitoso() {
		// 1. Preparamos el torneo simulado
		torneoMock.setEstado(Torneo.EstadoTorneo.INSCRIPCION);
		when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoMock));
		when(torneoRepository.save(any(Torneo.class))).thenReturn(torneoMock);

		// 2. Ejecutamos el nuevo método con su justificación
		torneoService.eliminarTorneoLogico(1L, "Cancelado por el administrador");

		// 3. Verificamos lo más básico: que cambió a ANULADO y se guardó
		assertEquals(Torneo.EstadoTorneo.ANULADO, torneoMock.getEstado());
		verify(torneoRepository).save(torneoMock);
	}

	@Test
	@DisplayName("Debería dar error si el torneo a eliminar no existe")
	void eliminarTorneoNoEncontrado() {
		// 1. Simulamos que la base de datos no encuentra el ID
		when(torneoRepository.findById(1L)).thenReturn(Optional.empty());

		// 2. Verificamos que lance la excepción simple
		assertThrows(TorneoNotFoundException.class, () -> {
			torneoService.eliminarTorneoLogico(1L, "Cancelado por el administrador");
		});
	}
}