package com.esports.registration_service;

import com.esports.registration_service.dto.InscripcionDTO;
import com.esports.registration_service.exception.InscripcionDuplicadaException;
import com.esports.registration_service.model.Inscripcion;
import com.esports.registration_service.repository.InscripcionRepository;
import com.esports.registration_service.service.InscripcionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

	@Mock
	private InscripcionRepository inscripcionRepository;

	@InjectMocks
	private InscripcionService inscripcionService;

	@Test
	void registrarEquipo_DebeCrearInscripcion_CuandoNoEsDuplicada() {
		InscripcionDTO.Request request = InscripcionDTO.Request.builder()
				.torneoId(10L)
				.equipoId(5L)
				.build();

		Inscripcion inscripcionGuardada = Inscripcion.builder()
				.id(1L)
				.torneoId(10L)
				.equipoId(5L)
				.estado(Inscripcion.EstadoInscripcion.PENDIENTE)
				.build();

		when(inscripcionRepository.findByTorneoId(10L)).thenReturn(Collections.emptyList());
		when(inscripcionRepository.save(any(Inscripcion.class))).thenReturn(inscripcionGuardada);

		InscripcionDTO.Response resultado = inscripcionService.registrarEquipo(request);

		assertNotNull(resultado);
		assertEquals(1L, resultado.getId());
		assertEquals("PENDIENTE", resultado.getEstado());
		assertNull(resultado.getMotivoCancelacion()); // Verifica el nuevo campo en estado inicial
		verify(inscripcionRepository, times(1)).save(any(Inscripcion.class));
	}

	@Test
	void registrarEquipo_DebeLanzarExcepcion_CuandoEquipoYaEstaInscrito() {
		InscripcionDTO.Request request = InscripcionDTO.Request.builder()
				.torneoId(10L)
				.equipoId(5L)
				.build();

		Inscripcion inscripcionExistente = Inscripcion.builder()
				.id(2L)
				.torneoId(10L)
				.equipoId(5L)
				.estado(Inscripcion.EstadoInscripcion.PENDIENTE)
				.build();

		when(inscripcionRepository.findByTorneoId(10L)).thenReturn(List.of(inscripcionExistente));

		assertThrows(InscripcionDuplicadaException.class, () -> {
			inscripcionService.registrarEquipo(request);
		});

		verify(inscripcionRepository, never()).save(any(Inscripcion.class));
	}
}