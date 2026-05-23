package com.esports.match_service;

import com.esports.match_service.dto.PartidaDTO;
import com.esports.match_service.exception.PartidaNotFoundException;
import com.esports.match_service.exception.ValidacionPartidaException;
import com.esports.match_service.model.Partida;
import com.esports.match_service.repository.PartidaRepository;
import com.esports.match_service.service.PartidaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartidaServiceTest {

	@Mock
	private PartidaRepository partidaRepository;

	@InjectMocks
	private PartidaService partidaService;

	@Test
	void crearPartida_DebeGuardarExitosamente_CuandoLosEquiposSonDistintos() {
		PartidaDTO.Request request = PartidaDTO.Request.builder()
				.torneoId(10L)
				.equipoLocalId(1L)
				.equipoVisitanteId(2L)
				.build();

		Partida partidaGuardada = Partida.builder()
				.id(100L)
				.torneoId(10L)
				.equipoLocalId(1L)
				.equipoVisitanteId(2L)
				.estado(Partida.EstadoPartida.PENDIENTE)
				.build();

		when(partidaRepository.save(any(Partida.class))).thenReturn(partidaGuardada);

		PartidaDTO.Response resultado = partidaService.crearPartida(request);

		assertNotNull(resultado);
		assertEquals(100L, resultado.getId());
		assertEquals("PENDIENTE", resultado.getEstado());
		assertEquals(1L, resultado.getEquipoLocalId());
		assertEquals(2L, resultado.getEquipoVisitanteId());
		verify(partidaRepository, times(1)).save(any(Partida.class));
	}

	@Test
	void crearPartida_DebeLanzarValidacionPartidaException_CuandoEquipoLocalYVisitanteSonIguales() {
		PartidaDTO.Request request = PartidaDTO.Request.builder()
				.torneoId(10L)
				.equipoLocalId(5L)
				.equipoVisitanteId(5L)
				.build();

		ValidacionPartidaException excepcion = assertThrows(ValidacionPartidaException.class, () -> {
			partidaService.crearPartida(request);
		});

		assertEquals("Un equipo no puede jugar contra sí mismo.", excepcion.getMessage());
		verify(partidaRepository, never()).save(any(Partida.class));
	}

	@Test
	void buscarPorId_DebeDevolverPartida_CuandoExiste() {
		Long partidaId = 100L;
		Partida partidaMock = Partida.builder()
				.id(partidaId)
				.torneoId(10L)
				.equipoLocalId(1L)
				.equipoVisitanteId(2L)
				.estado(Partida.EstadoPartida.EN_CURSO)
				.build();

		when(partidaRepository.findById(partidaId)).thenReturn(Optional.of(partidaMock));

		PartidaDTO.Response resultado = partidaService.buscarPorId(partidaId);

		assertNotNull(resultado);
		assertEquals(partidaId, resultado.getId());
		assertEquals("EN_CURSO", resultado.getEstado());
		verify(partidaRepository, times(1)).findById(partidaId);
	}

	@Test
	void buscarPorId_DebeLanzarPartidaNotFoundException_CuandoNoExiste() {
		Long idInvalido = 999L;
		when(partidaRepository.findById(idInvalido)).thenReturn(Optional.empty());

		PartidaNotFoundException excepcion = assertThrows(PartidaNotFoundException.class, () -> {
			partidaService.buscarPorId(idInvalido);
		});

		assertEquals("No se encontró el enfrentamiento con ID: 999", excepcion.getMessage());
	}

	@Test
	void actualizarResultado_DebeModificarMarcadorYEstado_CuandoDatosSonValidos() {
		Long partidaId = 100L;
		PartidaDTO.UpdateResultRequest updateRequest = PartidaDTO.UpdateResultRequest.builder()
				.resultado("16-12")
				.estado("FINALIZADA")
				.build();

		Partida partidaExistente = Partida.builder()
				.id(partidaId)
				.torneoId(10L)
				.equipoLocalId(1L)
				.equipoVisitanteId(2L)
				.estado(Partida.EstadoPartida.PENDIENTE)
				.build();

		when(partidaRepository.findById(partidaId)).thenReturn(Optional.of(partidaExistente));
		when(partidaRepository.save(any(Partida.class))).thenAnswer(invocation -> invocation.getArgument(0));

		PartidaDTO.Response resultado = partidaService.actualizarResultado(partidaId, updateRequest);

		assertNotNull(resultado);
		assertEquals("FINALIZADA", resultado.getEstado());
		assertEquals("16-12", resultado.getResultado());
		verify(partidaRepository, times(1)).findById(partidaId);
		verify(partidaRepository, times(1)).save(any(Partida.class));
	}

	@Test
	void actualizarResultado_DebeLanzarValidacionPartidaException_CuandoEstadoEsInvalido() {
		Long partidaId = 100L;
		PartidaDTO.UpdateResultRequest updateRequest = PartidaDTO.UpdateResultRequest.builder()
				.resultado("2-1")
				.estado("ESTADO_FALSO")
				.build();

		Partida partidaExistente = Partida.builder()
				.id(partidaId)
				.estado(Partida.EstadoPartida.PENDIENTE)
				.build();

		when(partidaRepository.findById(partidaId)).thenReturn(Optional.of(partidaExistente));

		ValidacionPartidaException excepcion = assertThrows(ValidacionPartidaException.class, () -> {
			partidaService.actualizarResultado(partidaId, updateRequest);
		});

		assertTrue(excepcion.getMessage().contains("Estado inválido. Los valores válidos son"));
		verify(partidaRepository, never()).save(any(Partida.class));
	}

	@Test
	void eliminarPartida_DebeBorrar_CuandoPartidaExiste() {
		Long partidaId = 100L;
		Partida partidaExistente = Partida.builder().id(partidaId).build();

		when(partidaRepository.findById(partidaId)).thenReturn(Optional.of(partidaExistente));
		doNothing().when(partidaRepository).delete(partidaExistente);

		partidaService.eliminarPartida(partidaId);

		verify(partidaRepository, times(1)).findById(partidaId);
		verify(partidaRepository, times(1)).delete(partidaExistente);
	}
}