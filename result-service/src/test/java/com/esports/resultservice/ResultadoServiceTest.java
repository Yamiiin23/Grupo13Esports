package com.esports.resultservice;

import com.esports.resultservice.client.MatchServiceClient;
import com.esports.resultservice.dto.ResultadoDTO;
import com.esports.resultservice.exception.ResultadoNotFoundException;
import com.esports.resultservice.model.Resultado;
import com.esports.resultservice.repository.ResultadoRepository;
import com.esports.resultservice.service.ResultadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock private ResultadoRepository resultadoRepository;
    @Mock private MatchServiceClient   matchClient;

    @InjectMocks private ResultadoService resultadoService;

    private Resultado resultadoBase;
    private ResultadoDTO.Request requestValido;
    private MatchServiceClient.PartidaResumen partidaEnCurso;

    @BeforeEach
    void setUp() {
        partidaEnCurso = new MatchServiceClient.PartidaResumen(
                1L, 1L, 10L, 20L, "Semifinal", "EN_CURSO", false);

        resultadoBase = Resultado.builder()
                .id(1L).partidaId(1L).ganadorId(10L)
                .puntajeA(2).puntajeB(0)
                .estadoValidacion(Resultado.EstadoValidacion.PENDIENTE)
                .fechaRegistro(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        requestValido = ResultadoDTO.Request.builder()
                .partidaId(1L).ganadorId(10L)
                .puntajeA(2).puntajeB(0)
                .build();
    }

    @Test
    @DisplayName("Debe registrar resultado con datos válidos")
    void crearResultado_exitoso() {
        when(matchClient.obtenerPartida(1L)).thenReturn(partidaEnCurso);
        when(resultadoRepository.existsByPartidaId(1L)).thenReturn(false);
        when(resultadoRepository.save(any())).thenReturn(resultadoBase);

        ResultadoDTO.Response result = resultadoService.crearResultado(requestValido);

        assertThat(result).isNotNull();
        assertThat(result.getEstadoValidacion()).isEqualTo("PENDIENTE");
        assertThat(result.isValidado()).isFalse();
        verify(resultadoRepository).save(any());
    }

    @Test
    @DisplayName("Debe rechazar si ya existe resultado para la partida")
    void crearResultado_duplicado() {
        when(matchClient.obtenerPartida(1L)).thenReturn(partidaEnCurso);
        when(resultadoRepository.existsByPartidaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> resultadoService.crearResultado(requestValido))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ya existe un resultado");

        verify(resultadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar si el ganador no es participante de la partida")
    void crearResultado_ganadorInvalido() {
        requestValido.setGanadorId(99L); // no es participante
        when(matchClient.obtenerPartida(1L)).thenReturn(partidaEnCurso);
        when(resultadoRepository.existsByPartidaId(1L)).thenReturn(false);

        assertThatThrownBy(() -> resultadoService.crearResultado(requestValido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no es participante");
    }

    @Test
    @DisplayName("Debe rechazar si la partida no está EN_CURSO o FINALIZADA")
    void crearResultado_partidaProgramada() {
        MatchServiceClient.PartidaResumen programada =
            new MatchServiceClient.PartidaResumen(1L, 1L, 10L, 20L, "Semifinal", "PROGRAMADA", true);
        when(matchClient.obtenerPartida(1L)).thenReturn(programada);

        assertThatThrownBy(() -> resultadoService.crearResultado(requestValido))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EN_CURSO o FINALIZADA");
    }


    @Test
    @DisplayName("Debe validar resultado y finalizar partida en match-service")
    void validarResultado_exitoso() {
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultadoBase));
        when(resultadoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(matchClient.cambiarEstadoPartida(eq(1L), any())).thenReturn(partidaEnCurso);

        ResultadoDTO.ValidacionRequest req = new ResultadoDTO.ValidacionRequest(
                Resultado.EstadoValidacion.VALIDADO, null);
        ResultadoDTO.Response result = resultadoService.cambiarValidacion(1L, req);

        assertThat(result.getEstadoValidacion()).isEqualTo("VALIDADO");
        assertThat(result.isValidado()).isTrue();
        // Verifica que se notificó a match-service
        verify(matchClient).cambiarEstadoPartida(eq(1L), any());
    }

    @Test
    @DisplayName("No debe modificar resultado ya VALIDADO")
    void actualizarResultado_validado_lanzaExcepcion() {
        resultadoBase.setEstadoValidacion(Resultado.EstadoValidacion.VALIDADO);
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultadoBase));

        assertThatThrownBy(() -> resultadoService.actualizarResultado(1L, requestValido))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("VALIDADO");
    }

    @Test
    @DisplayName("Anulación requiere justificación en observaciones")
    void anularResultado_sinJustificacion_lanzaExcepcion() {
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultadoBase));

        ResultadoDTO.ValidacionRequest req = new ResultadoDTO.ValidacionRequest(
                Resultado.EstadoValidacion.ANULADO, null); // sin justificación

        assertThatThrownBy(() -> resultadoService.cambiarValidacion(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("justificación");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el resultado no existe")
    void buscarPorId_noExiste() {
        when(resultadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultadoService.buscarPorId(99L))
                .isInstanceOf(ResultadoNotFoundException.class);
    }
}
