package com.esports.ranking_service;

import com.esports.ranking_service.client.TournamentServiceClient;
import com.esports.ranking_service.dto.RankingDTO;
import com.esports.ranking_service.model.Ranking;
import com.esports.ranking_service.repository.RankingRepository;
import com.esports.ranking_service.service.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class RankingServiceTest {

	@Mock
	private RankingRepository rankingRepository;

	@Mock
	private TournamentServiceClient tournamentClient;

	@InjectMocks
	private RankingService rankingService;

	private Ranking rankingMock;
	private RankingDTO.Request requestRegistro;
	private TournamentServiceClient.TorneoResumen torneoResumenMock;

	@BeforeEach
	void setUp() {
		rankingMock = Ranking.builder()
				.id(1L)
				.torneoId(100L)
				.participanteId(50L)
				.puntos(0)
				.victorias(0)
				.derrotas(0)
				.diferencia(0)
				.posicion(1)
				.build();

		requestRegistro = RankingDTO.Request.builder()
				.torneoId(100L)
				.participanteId(50L)
				.build();

		torneoResumenMock = new TournamentServiceClient.TorneoResumen(100L, "Torneo de Esports", "ACTIVO");
	}

	@Test
	@DisplayName("Debería registrar participante exitosamente")
	void registrarParticipanteExitoso() {
		when(tournamentClient.obtenerTorneoPorId(100L)).thenReturn(torneoResumenMock);
		when(rankingRepository.existsByTorneoIdAndParticipanteId(100L, 50L)).thenReturn(false);
		when(rankingRepository.save(any(Ranking.class))).thenReturn(rankingMock);
		when(rankingRepository.findByTorneoIdOrderByPosicion(100L)).thenReturn(List.of(rankingMock));

		RankingDTO.Response response = rankingService.registrarParticipante(requestRegistro);

		assertNotNull(response);
		assertEquals(1L, response.getId());
		verify(rankingRepository, times(1)).save(any(Ranking.class));
	}

	@Test
	@DisplayName("Debería lanzar IllegalStateException si el torneo no existe")
	void registrarParticipanteTorneoNoExiste() {
		when(tournamentClient.obtenerTorneoPorId(100L)).thenThrow(new RuntimeException("Error"));

		assertThrows(IllegalStateException.class, () -> {
			rankingService.registrarParticipante(requestRegistro);
		});
	}

	@Test
	@DisplayName("Debería retornar la tabla de posiciones")
	void obtenerTablaPosiciones() {
		when(rankingRepository.findByTorneoIdOrderByPosicion(100L)).thenReturn(List.of(rankingMock));

		List<RankingDTO.Response> tabla = rankingService.obtenerTabla(100L);

		assertNotNull(tabla);
		assertEquals(1, tabla.size());
	}

	@Test
	@DisplayName("Debería actualizar estadísticas al procesar un resultado")
	void actualizarConResultadoExitoso() {
		Long torneoId = 100L;
		Long ganadorId = 1L;
		Long perdedorId = 2L;

		RankingDTO.ActualizarRequest actualizarReq = RankingDTO.ActualizarRequest.builder()
				.ganadorId(ganadorId)
				.perdedorId(perdedorId)
				.puntajeGanador(16)
				.puntajePerdedor(10)
				.build();

		Ranking rGanador = Ranking.builder().id(ganadorId).torneoId(torneoId).participanteId(ganadorId).puntos(0).victorias(0).derrotas(0).diferencia(0).build();
		Ranking rPerdedor = Ranking.builder().id(perdedorId).torneoId(torneoId).participanteId(perdedorId).puntos(0).victorias(0).derrotas(0).diferencia(0).build();

		when(rankingRepository.findByTorneoIdAndParticipanteId(torneoId, ganadorId)).thenReturn(Optional.of(rGanador));
		when(rankingRepository.findByTorneoIdAndParticipanteId(torneoId, perdedorId)).thenReturn(Optional.of(rPerdedor));
		when(rankingRepository.findByTorneoIdOrderByPosicion(torneoId)).thenReturn(List.of(rGanador, rPerdedor));

		List<RankingDTO.Response> resultado = rankingService.actualizarConResultado(torneoId, actualizarReq);

		assertNotNull(resultado);
		assertEquals(3, rGanador.getPuntos());
		assertEquals(6, rGanador.getDiferencia());
		verify(rankingRepository, times(1)).save(rGanador);
	}
}