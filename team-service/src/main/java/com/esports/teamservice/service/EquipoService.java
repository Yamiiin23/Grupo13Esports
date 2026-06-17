package com.esports.teamservice.service;

import com.esports.teamservice.client.ClientDTO;
import com.esports.teamservice.client.GameClient;
import com.esports.teamservice.client.UserClient;
import com.esports.teamservice.dto.EquipoDTO;
import com.esports.teamservice.exception.*;
import com.esports.teamservice.model.Equipo;
import com.esports.teamservice.model.MiembroEquipo;
import com.esports.teamservice.repository.EquipoRepository;
import com.esports.teamservice.repository.MiembroEquipoRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EquipoService {

    private static final Logger log = LoggerFactory.getLogger(EquipoService.class);

    private final EquipoRepository equipoRepository;
    private final MiembroEquipoRepository miembroRepository;
    private final UserClient userClient;
    private final GameClient gameClient;

    public EquipoService(EquipoRepository equipoRepository,
                         MiembroEquipoRepository miembroRepository,
                         UserClient userClient,
                         GameClient gameClient) {
        this.equipoRepository  = equipoRepository;
        this.miembroRepository = miembroRepository;
        this.userClient        = userClient;
        this.gameClient        = gameClient;
    }

    public EquipoDTO.Response crearEquipo(EquipoDTO.Request request) {
        log.info("Creando equipo nuevo: {}", request.getNombre());

        if (equipoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new EquipoYaExisteException(request.getNombre());
        }

        validarUsuarioPuedeCompetar(request.getCapitanId(), "Capitán");
        validarJuegoActivo(request.getJuegoPrincipalId());

        Equipo equipo = Equipo.builder()
                .nombre(request.getNombre())
                .capitanId(request.getCapitanId())
                .juegoPrincipalId(request.getJuegoPrincipalId())
                .estado(Equipo.EstadoEquipo.ACTIVO)
                .build();

        Equipo guardado = equipoRepository.save(equipo);

        MiembroEquipo miembroCapitan = MiembroEquipo.builder()
                .equipo(guardado)
                .usuarioId(request.getCapitanId())
                .rolDentroEquipo(MiembroEquipo.RolEnEquipo.CAPITAN)
                .build();

        miembroRepository.save(miembroCapitan);
        guardado.getMiembros().add(miembroCapitan);

        return EquipoDTO.Response.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public List<EquipoDTO.Response> listarEquipos(Long juegoId, Equipo.EstadoEquipo estado) {
        List<Equipo> equipos;
        if (juegoId != null && estado != null) {
            equipos = equipoRepository.findByJuegoPrincipalIdAndEstado(juegoId, estado);
        } else if (juegoId != null) {
            equipos = equipoRepository.findByJuegoPrincipalId(juegoId);
        } else if (estado != null) {
            equipos = equipoRepository.findByEstado(estado);
        } else {
            equipos = equipoRepository.findAll();
        }
        return equipos.stream().map(EquipoDTO.Response::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public EquipoDTO.Response buscarPorId(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new EquipoNotFoundException(id));
        return EquipoDTO.Response.fromEntity(equipo);
    }

    public EquipoDTO.Response actualizarEquipo(Long id, EquipoDTO.Request request) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new EquipoNotFoundException(id));

        if (!equipo.getNombre().equalsIgnoreCase(request.getNombre())
                && equipoRepository.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), id)) {
            throw new EquipoYaExisteException(request.getNombre());
        }

        if (!equipo.getCapitanId().equals(request.getCapitanId())) {
            validarUsuarioPuedeCompetar(request.getCapitanId(), "Nuevo capitán");
        }

        if (!equipo.getJuegoPrincipalId().equals(request.getJuegoPrincipalId())) {
            validarJuegoActivo(request.getJuegoPrincipalId());
        }

        equipo.setNombre(request.getNombre());
        equipo.setCapitanId(request.getCapitanId());
        equipo.setJuegoPrincipalId(request.getJuegoPrincipalId());

        return EquipoDTO.Response.fromEntity(equipoRepository.save(equipo));
    }

    public EquipoDTO.Response agregarMiembro(Long equipoId, EquipoDTO.MiembroRequest request) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new EquipoNotFoundException(equipoId));

        if (!equipo.getEstado().puedeInscribirse()) {
            throw new IllegalStateException("No se permiten agregar miembros si el equipo esta INACTIVO");
        }

        validarUsuarioPuedeCompetar(request.getUsuarioId(), "Jugador");

        if (miembroRepository.existsByEquipoIdAndUsuarioId(equipoId, request.getUsuarioId())) {
            throw new MiembroDuplicadoException(request.getUsuarioId(), equipoId);
        }

        MiembroEquipo.RolEnEquipo rol = request.getRolDentroEquipo() != null
                ? request.getRolDentroEquipo()
                : MiembroEquipo.RolEnEquipo.JUGADOR;

        MiembroEquipo miembro = MiembroEquipo.builder()
                .equipo(equipo)
                .usuarioId(request.getUsuarioId())
                .rolDentroEquipo(rol)
                .build();

        miembroRepository.save(miembro);
        equipo.getMiembros().add(miembro);

        return EquipoDTO.Response.fromEntity(equipo);
    }

    public EquipoDTO.Response eliminarMiembro(Long equipoId, Long usuarioId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new EquipoNotFoundException(equipoId));

        if (equipo.getCapitanId().equals(usuarioId)) {
            throw new IllegalStateException("No se puede eliminar al capitan. Cambie la capitania primero.");
        }

        MiembroEquipo miembro = equipo.getMiembros().stream()
                .filter(m -> m.getUsuarioId().equals(usuarioId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El usuario no pertenece a este equipo"));

        equipo.getMiembros().remove(miembro);
        miembroRepository.delete(miembro);

        return EquipoDTO.Response.fromEntity(equipo);
    }

    public EquipoDTO.Response desactivarEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new EquipoNotFoundException(id));

        equipo.setEstado(Equipo.EstadoEquipo.INACTIVO);
        return EquipoDTO.Response.fromEntity(equipoRepository.save(equipo));
    }

    private void validarUsuarioPuedeCompetar(Long usuarioId, String rol) {
        try {
            ClientDTO.UsuarioResumen usuario = userClient.findById(usuarioId);
            if (!usuario.isPuedeCompetar()) {
                throw new IllegalStateException(rol + " seleccionado no esta disponible para competir");
            }
        } catch (FeignException.NotFound e) {
            throw new ServicioExternoException("user-service", "Usuario con ID " + usuarioId + " no existe");
        } catch (FeignException e) {
            throw new ServicioExternoException("user-service", "Error de comunicacion con el servicio de usuarios");
        }
    }

    private void validarJuegoActivo(Long juegoId) {
        try {
            ClientDTO.JuegoResumen juego = gameClient.findById(juegoId);
            if (!"ACTIVO".equals(juego.getEstado())) {
                throw new IllegalStateException("El juego seleccionado no esta activo");
            }
        } catch (FeignException.NotFound e) {
            throw new ServicioExternoException("game-service", "Juego con ID " + juegoId + " no existe");
        } catch (FeignException e) {
            throw new ServicioExternoException("game-service", "Error de comunicacion con el servicio de juegos");
        }
    }
}