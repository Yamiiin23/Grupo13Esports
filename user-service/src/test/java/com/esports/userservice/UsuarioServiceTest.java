package com.esports.userservice;

import com.esports.userservice.dto.UsuarioDTO;
import com.esports.userservice.exception.UsuarioDuplicadoException;
import com.esports.userservice.exception.UsuarioNotFoundException;
import com.esports.userservice.model.Usuario;
import com.esports.userservice.repository.UsuarioRepository;
import com.esports.userservice.service.UsuarioService;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioBase;
    private UsuarioDTO.Request requestValido;

    @BeforeEach
    void setUp() {
        usuarioBase = Usuario.builder()
                .id(1L)
                .nombre("Carlos Pérez")
                .nickname("CarlosGG")
                .email("carlos@esports.cl")
                .rol(Usuario.RolUsuario.JUGADOR)
                .estado(Usuario.EstadoUsuario.ACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        requestValido = UsuarioDTO.Request.builder()
                .nombre("Carlos Pérez")
                .nickname("CarlosGG")
                .email("carlos@esports.cl")
                .rol(Usuario.RolUsuario.JUGADOR)
                .build();
    }

    @Test
    @DisplayName("Debe crear usuario cuando nickname y email son únicos")
    void crearUsuario_exitoso() {
        when(usuarioRepository.existsByNicknameIgnoreCase("CarlosGG")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("carlos@esports.cl")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBase);

        UsuarioDTO.Response result = usuarioService.crearUsuario(requestValido);

        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo("CarlosGG");
        assertThat(result.getEstado()).isEqualTo("ACTIVO");
        assertThat(result.isPuedeCompetar()).isTrue();
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el nickname ya existe")
    void crearUsuario_nicknameExistente_lanzaExcepcion() {
        when(usuarioRepository.existsByNicknameIgnoreCase("CarlosGG")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(requestValido))
                .isInstanceOf(UsuarioDuplicadoException.class)
                .hasMessageContaining("CarlosGG");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email ya existe")
    void crearUsuario_emailExistente_lanzaExcepcion() {
        when(usuarioRepository.existsByNicknameIgnoreCase("CarlosGG")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("carlos@esports.cl")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(requestValido))
                .isInstanceOf(UsuarioDuplicadoException.class)
                .hasMessageContaining("carlos@esports.cl");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe retornar usuario existente por ID")
    void buscarPorId_exitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        UsuarioDTO.Response result = usuarioService.buscarPorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNickname()).isEqualTo("CarlosGG");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el ID no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Usuario SANCIONADO no puede competir")
    void usuarioSancionado_noPuedeCompetar() {
        usuarioBase.setEstado(Usuario.EstadoUsuario.SANCIONADO);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        UsuarioDTO.Response result = usuarioService.buscarPorId(1L);

        assertThat(result.getEstado()).isEqualTo("SANCIONADO");
        assertThat(result.isPuedeCompetar()).isFalse();
    }

    @Test
    @DisplayName("Usuario INACTIVO no puede competir")
    void usuarioInactivo_noPuedeCompetar() {
        usuarioBase.setEstado(Usuario.EstadoUsuario.INACTIVO);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        UsuarioDTO.Response result = usuarioService.buscarPorId(1L);

        assertThat(result.isPuedeCompetar()).isFalse();
    }

    @Test
    @DisplayName("Debe cambiar estado correctamente")
    void actualizarEstado_exitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO.Response result = usuarioService.actualizarEstado(1L, Usuario.EstadoUsuario.SANCIONADO);

        assertThat(result.getEstado()).isEqualTo("SANCIONADO");
        assertThat(result.isPuedeCompetar()).isFalse();
    }

    @Test
    @DisplayName("Debe desactivar usuario correctamente")
    void desactivarUsuario_exitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO.Response result = usuarioService.desactivarUsuario(1L);

        assertThat(result.getEstado()).isEqualTo("INACTIVO");
        assertThat(result.isPuedeCompetar()).isFalse();
    }

    @Test
    @DisplayName("Debe listar todos los usuarios sin filtros")
    void listarUsuarios_sinFiltros() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioBase));

        List<UsuarioDTO.Response> result = usuarioService.listarUsuarios(null, null);

        assertThat(result).hasSize(1);
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Debe listar solo jugadores cuando rol=JUGADOR")
    void listarUsuarios_porRol() {
        when(usuarioRepository.findByRol(Usuario.RolUsuario.JUGADOR)).thenReturn(List.of(usuarioBase));

        List<UsuarioDTO.Response> result = usuarioService.listarUsuarios(Usuario.RolUsuario.JUGADOR, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRol()).isEqualTo("JUGADOR");
    }
}
