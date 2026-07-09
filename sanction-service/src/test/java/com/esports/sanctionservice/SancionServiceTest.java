package com.esports.sanctionservice;

import com.esports.sanctionservice.client.TeamServiceClient;
import com.esports.sanctionservice.client.UserServiceClient;
import com.esports.sanctionservice.dto.SancionDTO;
import com.esports.sanctionservice.exception.SancionNotFoundException;
import com.esports.sanctionservice.exception.SancionValidationException;
import com.esports.sanctionservice.model.Sancion;
import com.esports.sanctionservice.repository.SancionRepository;
import com.esports.sanctionservice.service.SancionService;
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
@DisplayName("Pruebas Unitarias del Módulo SancionService")
class SancionServiceTest {
    @Mock private SancionRepository sancionRepository;
    @Mock private UserServiceClient userClient;
    @Mock private TeamServiceClient teamClient;

    @InjectMocks
    private SancionService sancionService;

    private Sancion sancionBase;
    private SancionDTO.Request requestValido;

    @BeforeEach
    void setUp() {
        LocalDateTime ahora = LocalDateTime.now();

        sancionBase = Sancion.builder()
                .id(1L)
                .usuarioId(10L)
                .motivo("Conducta antideportiva")
                .fechaInicio(ahora.minusDays(1))
                .fechaFin(ahora.plusDays(7))
                .estado(Sancion.EstadoSancion.ACTIVA)
                .severidad(Sancion.SeveridadSancion.SUSPENSION)
                .fechaCreacion(ahora)
                .fechaActualizacion(ahora)
                .build();

        requestValido = SancionDTO.Request.builder()
                .usuarioId(10L)
                .motivo("Conducta antideportiva")
                .fechaInicio(ahora.minusDays(1))
                .fechaFin(ahora.plusDays(7))
                .severidad(Sancion.SeveridadSancion.SUSPENSION)
                .build();
    }

    @Test
    @DisplayName("Debe crear sanción con datos válidos")
    void crearSancion_exitoso() {
        when(userClient.obtenerResumenUsuario(10L))
                .thenReturn(new UserServiceClient.UsuarioResumen(10L, "GamerX", "JUGADOR", "ACTIVO", true));
        when(sancionRepository.save(any(Sancion.class))).thenReturn(sancionBase);

        SancionDTO.Response result = sancionService.crearSancion(requestValido);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("ACTIVA");
        assertThat(result.isBloqueante()).isTrue();
        verify(sancionRepository).save(any(Sancion.class));
    }

    @Test
    @DisplayName("Debe fallar si no hay destinatario")
    void crearSancion_sinDestinatario_lanzaExcepcion() {
        requestValido.setUsuarioId(null);
        requestValido.setEquipoId(null);

        assertThatThrownBy(() -> sancionService.crearSancion(requestValido))
                .isInstanceOf(SancionValidationException.class)
                .hasMessageContaining("destinatario");

        verify(sancionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar si fechaFin no es posterior a fechaInicio")
    void crearSancion_fechaInvalida() {
        requestValido.setFechaFin(requestValido.getFechaInicio().minusDays(1));

        assertThatThrownBy(() -> sancionService.crearSancion(requestValido))
                .isInstanceOf(SancionValidationException.class)
                .hasMessageContaining("posterior");
    }

    @Test
    @DisplayName("verificarBloqueoUsuario devuelve bloqueado=true cuando hay sanción activa")
    void verificarBloqueoUsuario_bloqueado() {
        when(sancionRepository.findSancionesBloqueantesUsuario(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(sancionBase));

        SancionDTO.VerificacionResponse result = sancionService.verificarBloqueoUsuario(10L);

        assertThat(result.isBloqueado()).isTrue();
        assertThat(result.getMotivo()).isEqualTo("Conducta antideportiva");
    }

    @Test
    @DisplayName("verificarBloqueoUsuario devuelve bloqueado=false cuando no hay sanciones")
    void verificarBloqueoUsuario_libre() {
        when(sancionRepository.findSancionesBloqueantesUsuario(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of());

        SancionDTO.VerificacionResponse result = sancionService.verificarBloqueoUsuario(10L);

        assertThat(result.isBloqueado()).isFalse();
    }

    @Test
    @DisplayName("Debe cerrar sanción correctamente")
    void cerrarSancion_exitoso() {
        when(sancionRepository.findById(1L)).thenReturn(Optional.of(sancionBase));
        when(sancionRepository.save(any(Sancion.class))).thenAnswer(inv -> inv.getArgument(0));

        SancionDTO.Response result = sancionService.cerrarSancion(1L, "Cumplida");

        assertThat(result.getEstado()).isEqualTo("CERRADA");
        assertThat(result.isBloqueante()).isFalse();
    }

    @Test
    @DisplayName("No debe cerrar una sanción ya cerrada")
    void cerrarSancion_yaCerrada_lanzaExcepcion() {
        sancionBase.setEstado(Sancion.EstadoSancion.CERRADA);
        when(sancionRepository.findById(1L)).thenReturn(Optional.of(sancionBase));
        assertThatThrownBy(() -> sancionService.cerrarSancion(1L, "x"))
                .isInstanceOf(SancionValidationException.class)
                .hasMessageContaining("CERRADA");
    }

    @Test
    @DisplayName("Debe lanzar excepción si la sanción no existe")
    void buscarPorId_noExiste() {
        when(sancionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sancionService.buscarPorId(99L))
                .isInstanceOf(SancionNotFoundException.class);
    }
}