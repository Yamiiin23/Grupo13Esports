package com.esports.rankingservice;

import com.esports.rankingservice.client.ResultServiceClient;
import com.esports.rankingservice.client.TournamentServiceClient;
import com.esports.rankingservice.dto.RankingDTO;
import com.esports.rankingservice.exception.RankingNotFoundException;
import com.esports.rankingservice.model.Ranking;
import com.esports.rankingservice.repository.RankingRepository;
import com.esports.rankingservice.service.RankingService;
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
class RankingServiceTest {

    @Mock private RankingRepository       rankingRepository;
    @Mock private ResultServiceClient     resultClient;
    @Mock private TournamentServiceClient tournamentClient;

    @InjectMocks private RankingService rankingService;

    private Ranking rankingBase;
    private TournamentServiceClient.TorneoResumen torneoValido;

    @BeforeEach
    void setUp() {
        torneoValido = new TournamentServiceClient.TorneoResumen(1L, "Copa 2025", "EN_CURSO", false);

        rankingBase = Ranking.builder()
                .id(1L).torneoId(1L).participanteId(10L)
                .puntos(0).victorias(0).derrotas(0).diferencia(0).posicion(1)
                .fechaCreacion(LocalDateTime.now()).fechaActualizacion(LocalDateTime.now())
                .build();
    }

    // ── Tests de registro ────────────────────────────────

    @Test
    @DisplayName("Debe registrar participante en ranking correctamente")
    void registrarParticipante_exitoso() {
        when(tournamentClient.obtenerResumen(1L)).thenReturn(torneoValido);
        when(rankingRepository.existsByTorneoIdAndParticipanteId(1L, 10L)).thenReturn(false);
        when(rankingRepository.save(any())).thenReturn(rankingBase);
        when(rankingRepository.findByTorneoIdOrderByPosicion(1L)).thenReturn(List.of(rankingBase));
        when(rankingRepository.saveAll(any())).thenReturn(List.of(rankingBase));

        RankingDTO.Request request = new RankingDTO.Request(1L, 10L);
        RankingDTO.Response result = rankingService.registrarParticipante(request);

        assertThat(result).isNotNull();
        assertThat(result.getParticipanteId()).isEqualTo(10L);
        verify(rankingRepository).save(any());
    }

    @Test
    @DisplayName("Debe rechazar participante duplicado en el mismo torneo")
    void registrarParticipante_duplicado() {
        when(tournamentClient.obtenerResumen(1L)).thenReturn(torneoValido);
        when(rankingRepository.existsByTorneoIdAndParticipanteId(1L, 10L)).thenReturn(true);

        RankingDTO.Request request = new RankingDTO.Request(1L, 10L);
        assertThatThrownBy(() -> rankingService.registrarParticipante(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya está registrado");

        verify(rankingRepository, never()).save(any());
    }

    // ── Tests de actualización con resultado ─────────────

    @Test
    @DisplayName("Debe actualizar puntos del ganador y recalcular posiciones")
    void actualizarConResultado_exitoso() {
        Ranking rankingPerdedor = Ranking.builder()
                .id(2L).torneoId(1L).participanteId(20L)
                .puntos(0).victorias(0).derrotas(0).diferencia(0).posicion(2)
                .fechaCreacion(LocalDateTime.now()).fechaActualizacion(LocalDateTime.now())
                .build();

        when(rankingRepository.findByTorneoIdAndParticipanteId(1L, 10L))
                .thenReturn(Optional.of(rankingBase));
        when(rankingRepository.findByTorneoIdAndParticipanteId(1L, 20L))
                .thenReturn(Optional.of(rankingPerdedor));
        when(rankingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rankingRepository.findByTorneoIdOrderByPosicion(1L))
                .thenReturn(List.of(rankingBase, rankingPerdedor));
        when(rankingRepository.saveAll(any())).thenReturn(List.of(rankingBase, rankingPerdedor));
        when(rankingRepository.findById(1L)).thenReturn(Optional.of(rankingBase));
        when(rankingRepository.findById(2L)).thenReturn(Optional.of(rankingPerdedor));

        RankingDTO.ActualizarRequest request = new RankingDTO.ActualizarRequest(10L, 20L, 2, 0);
        List<RankingDTO.Response> result = rankingService.actualizarConResultado(1L, request);

        assertThat(result).hasSize(2);
        // Verificar que se guardó el ganador con más puntos
        verify(rankingRepository, atLeast(2)).save(any());
        // Verificar que se recalcularon posiciones
        verify(rankingRepository).saveAll(any());
    }

    @Test
    @DisplayName("Debe recalcular posiciones correctamente: más puntos = posición más alta")
    void registrarVictoria_actualizaPuntos() {
        rankingBase.registrarVictoria(2, 0);

        assertThat(rankingBase.getPuntos()).isEqualTo(3);
        assertThat(rankingBase.getVictorias()).isEqualTo(1);
        assertThat(rankingBase.getDiferencia()).isEqualTo(2);
    }

    @Test
    @DisplayName("Derrota no suma puntos pero sí actualiza diferencia")
    void registrarDerrota_actualizaDiferencia() {
        rankingBase.registrarDerrota(0, 2);

        assertThat(rankingBase.getPuntos()).isEqualTo(0);
        assertThat(rankingBase.getDerrotas()).isEqualTo(1);
        assertThat(rankingBase.getDiferencia()).isEqualTo(-2);
    }

    // ── Tests de lectura ─────────────────────────────────

    @Test
    @DisplayName("Debe lanzar excepción si no existe el ranking")
    void buscarPorId_noExiste() {
        when(rankingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rankingService.buscarPorId(99L))
                .isInstanceOf(RankingNotFoundException.class);
    }

    @Test
    @DisplayName("Debe retornar tabla de posiciones del torneo")
    void obtenerTabla_exitoso() {
        when(rankingRepository.findByTorneoIdOrderByPosicion(1L))
                .thenReturn(List.of(rankingBase));

        List<RankingDTO.Response> result = rankingService.obtenerTabla(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTorneoId()).isEqualTo(1L);
    }
}
