package com.esports.teamservice;

import com.esports.teamservice.dto.ClientDTO;
import com.esports.teamservice.client.GameClient;
import com.esports.teamservice.client.UserClient;
import com.esports.teamservice.dto.EquipoDTO;
import com.esports.teamservice.exception.EquipoNotFoundException;
import com.esports.teamservice.exception.MiembroDuplicadoException;
import com.esports.teamservice.model.Equipo;
import com.esports.teamservice.model.MiembroEquipo;
import com.esports.teamservice.repository.EquipoRepository;
import com.esports.teamservice.repository.MiembroEquipoRepository;
import com.esports.teamservice.service.EquipoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias del Módulo EquipoService - Estructura AAA")
class EquipoServiceTest {

    @Mock private EquipoRepository       equipoRepository;
    @Mock private MiembroEquipoRepository miembroRepository;
    @Mock private UserClient userClient;
    @Mock private GameClient gameClient;

    @InjectMocks
    private EquipoService equipoService;

    private Equipo equipoBase;
    private EquipoDTO.Request requestValido;
    private ClientDTO.UsuarioResumen usuarioActivo;
    private ClientDTO.JuegoResumen juegoActivo;

    @BeforeEach
    void setUp() {
        equipoBase = Equipo.builder()
                .id(1L)
                .nombre("Team Phantom")
                .capitanId(10L)
                .juegoPrincipalId(5L)
                .estado(Equipo.EstadoEquipo.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .miembros(new ArrayList<>())
                .build();

        requestValido = EquipoDTO.Request.builder()
                .nombre("Team Phantom")
                .capitanId(10L)
                .juegoPrincipalId(5L)
                .build();

        usuarioActivo = new ClientDTO.UsuarioResumen(10L, "CapitanGG", "JUGADOR", "ACTIVO", true);
        juegoActivo   = new ClientDTO.JuegoResumen(5L, "Valorant", "5v5", 5, "ACTIVO");
    }

    @Test
    @DisplayName("Debe crear equipo cuando todos los datos son válidos")
    void crearEquipo_exitoso() {
        // --- 1. GIVEN (Arrange - Configuración) ---
        when(equipoRepository.existsByNombreIgnoreCase("Team Phantom")).thenReturn(false);
        when(userClient.findById(10L)).thenReturn(usuarioActivo);
        when(gameClient.findById(5L)).thenReturn(juegoActivo);
        when(equipoRepository.save(any(Equipo.class))).thenReturn(equipoBase);
        when(miembroRepository.save(any(MiembroEquipo.class))).thenAnswer(inv -> inv.getArgument(0));

        // --- 2. WHEN (Act - Ejecución) ---
        EquipoDTO.Response result = equipoService.crearEquipo(requestValido);

        // --- 3. THEN (Assert - Verificación) ---
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Team Phantom");
        assertThat(result.getEstado()).isEqualTo("ACTIVO");
        assertThat(result.isPuedeInscribirse()).isTrue();

        verify(userClient).findById(10L);
        verify(gameClient).findById(5L);
        verify(equipoRepository).save(any(Equipo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el capitán no puede competir")
    void crearEquipo_capitanSancionado_lanzaExcepcion() {
        // --- 1. GIVEN (Arrange) ---
        when(equipoRepository.existsByNombreIgnoreCase("Team Phantom")).thenReturn(false);
        ClientDTO.UsuarioResumen sancionado = new ClientDTO.UsuarioResumen(10L, "SancionadoGG", "JUGADOR", "SANCIONADO", false);
        when(userClient.findById(10L)).thenReturn(sancionado);

        // --- 2. WHEN & 3. THEN (Act & Assert combinados para excepciones) ---
        assertThatThrownBy(() -> equipoService.crearEquipo(requestValido))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no puede competir");

        verify(equipoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el juego está INACTIVO")
    void crearEquipo_juegoInactivo_lanzaExcepcion() {
        // --- 1. GIVEN (Arrange) ---
        when(equipoRepository.existsByNombreIgnoreCase("Team Phantom")).thenReturn(false);
        when(userClient.findById(10L)).thenReturn(usuarioActivo);
        ClientDTO.JuegoResumen inactivo = new ClientDTO.JuegoResumen(5L, "Juego Viejo", "1v1", 1, "INACTIVO");
        when(gameClient.findById(5L)).thenReturn(inactivo);

        // --- 2. WHEN & 3. THEN (Act & Assert combinados) ---
        assertThatThrownBy(() -> equipoService.crearEquipo(requestValido))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no está activo");

        verify(equipoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe retornar equipo existente por ID")
    void buscarPorId_exitoso() {
        // --- 1. GIVEN (Arrange) ---
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoBase));

        // --- 2. WHEN (Act) ---
        EquipoDTO.Response result = equipoService.buscarPorId(1L);

        // --- 3. THEN (Assert) ---
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Team Phantom");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el equipo no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        // --- 1. GIVEN (Arrange) ---
        when(equipoRepository.findById(99L)).thenReturn(Optional.empty());

        // --- 2. WHEN & 3. THEN (Act & Assert combinados) ---
        assertThatThrownBy(() -> equipoService.buscarPorId(99L))
                .isInstanceOf(EquipoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Debe agregar miembro cuando el usuario puede competir y no está duplicado")
    void agregarMiembro_exitoso() {
        // --- 1. GIVEN (Arrange) ---
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoBase));
        ClientDTO.UsuarioResumen nuevoJugador = new ClientDTO.UsuarioResumen(20L, "NuevoGG", "JUGADOR", "ACTIVO", true);
        when(userClient.findById(20L)).thenReturn(nuevoJugador);
        when(miembroRepository.existsByEquipoIdAndUsuarioId(1L, 20L)).thenReturn(false);
        when(miembroRepository.save(any(MiembroEquipo.class))).thenAnswer(inv -> inv.getArgument(0));

        EquipoDTO.MiembroRequest miembroRequest = EquipoDTO.MiembroRequest.builder()
                .usuarioId(20L).rolDentroEquipo(MiembroEquipo.RolEnEquipo.JUGADOR).build();

        // --- 2. WHEN (Act) ---
        EquipoDTO.Response result = equipoService.agregarMiembro(1L, miembroRequest);

        // --- 3. THEN (Assert) ---
        assertThat(result).isNotNull();
        verify(miembroRepository).save(any(MiembroEquipo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el miembro ya está en el equipo")
    void agregarMiembro_duplicado_lanzaExcepcion() {
        // --- 1. GIVEN (Arrange) ---
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoBase));
        when(userClient.findById(10L)).thenReturn(usuarioActivo);
        when(miembroRepository.existsByEquipoIdAndUsuarioId(1L, 10L)).thenReturn(true);

        EquipoDTO.MiembroRequest miembroRequest = EquipoDTO.MiembroRequest.builder()
                .usuarioId(10L).build();

        // --- 2. WHEN & 3. THEN (Act & Assert combinados) ---
        assertThatThrownBy(() -> equipoService.agregarMiembro(1L, miembroRequest))
                .isInstanceOf(MiembroDuplicadoException.class);

        verify(miembroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe desactivar equipo correctamente")
    void desactivarEquipo_exitoso() {
        // --- 1. GIVEN (Arrange) ---
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoBase));
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(inv -> inv.getArgument(0));

        // --- 2. WHEN (Act) ---
        EquipoDTO.Response result = equipoService.desactivarEquipo(1L);

        // --- 3. THEN (Assert) ---
        assertThat(result.getEstado()).isEqualTo("INACTIVO");
        assertThat(result.isPuedeInscribirse()).isFalse();
    }
}