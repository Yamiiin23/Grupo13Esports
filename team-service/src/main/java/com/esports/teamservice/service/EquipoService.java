package com.esports.teamservice.service;

import com.esports.teamservice.client.ClientDTO;
import com.esports.teamservice.client.GameServiceClient;
import com.esports.teamservice.client.UserServiceClient;
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

    private final EquipoRepository       equipoRepository;
    private final MiembroEquipoRepository miembroRepository;
    private final UserServiceClient       userClient;
    private final GameServiceClient       gameClient;

    public EquipoService(EquipoRepository equipoRepository,
                         MiembroEquipoRepository miembroRepository,
                         UserServiceClient userClient,
                         GameServiceClient gameClient) {
        this.equipoRepository  = equipoRepository;
        this.miembroRepository = miembroRepository;
        this.userClient        = userClient;
        this.gameClient        = gameClient;
    }

    public EquipoDTO.Response crearEquipo(EquipoDTO.Request request) {
        log.info("[team-service] Creando equipo: nombre={}, capitanId={}, juegoId={}",
                request.getNombre(), request.getCapitanId(), request.getJuegoPrincipalId());

        if (equipoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            log.warn("[team-service] Nombre de equipo duplicado: {}", request.getNombre());
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

        log.info("[team-service] Equipo creado. ID={}, nombre={}", guardado.getId(), guardado.getNombre());
        return EquipoDTO.Response.fromEntity(guardado);
    }


    @Transactional(readOnly = true)
    public List<EquipoDTO.Response> listarEquipos(Long juegoId, Equipo.EstadoEquipo estado) {
        log.info("[team-service] Listando equipos. juegoId={}, estado={}", juegoId, estado);

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
        log.info("[team-service] Buscando equipo ID={}", id);
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[team-service] Equipo no encontrado. ID={}", id);
                    return new EquipoNotFoundException(id);
                });
        return EquipoDTO.Response.fromEntity(equipo);
    }

    public EquipoDTO.Response actualizarEquipo(Long id, EquipoDTO.Request request) {
        log.info("[team-service] Actualizando equipo ID={}", id);

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

        Equipo actualizado = equipoRepository.save(equipo);
        log.info("[team-service] Equipo actualizado. ID={}", actualizado.getId());
        return EquipoDTO.Response.fromEntity(actualizado);
    }

    public EquipoDTO.Response agregarMiembro(Long equipoId, EquipoDTO.MiembroRequest request) {
        log.info("[team-service] Agregando miembro usuarioId={} al equipo ID={}", request.getUsuarioId(), equipoId);

        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new EquipoNotFoundException(equipoId));

        if (!equipo.getEstado().puedeInscribirse()) {
            log.warn("[team-service] No se puede agregar miembro a equipo INACTIVO. ID={}", equipoId);
            throw new IllegalStateException("No se pueden agregar miembros a un equipo INACTIVO");
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

        log.info("[team-service] Miembro agregado. usuarioId={}, equipoId={}, rol={}", request.getUsuarioId(), equipoId, rol);
        return EquipoDTO.Response.fromEntity(equipo);
    }

    public EquipoDTO.Response eliminarMiembro(Long equipoId, Long usuarioId) {
        log.info("[team-service] Eliminando miembro usuarioId={} del equipo ID={}", usuarioId, equipoId);

        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new EquipoNotFoundException(equipoId));

        if (equipo.getCapitanId().equals(usuarioId)) {
            throw new IllegalStateException("No se puede eliminar al capitán del equipo. Cambia el capitán primero.");
        }

        MiembroEquipo miembro = equipo.getMiembros().stream()
                .filter(m -> m.getUsuarioId().equals(usuarioId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El usuario ID=" + usuarioId + " no es miembro del equipo"));

        equipo.getMiembros().remove(miembro);
        miembroRepository.delete(miembro);

        log.info("[team-service] Miembro eliminado. usuarioId={}, equipoId={}", usuarioId, equipoId);
        return EquipoDTO.Response.fromEntity(equipo);
    }

    public EquipoDTO.Response desactivarEquipo(Long id) {
        log.info("[team-service] Desactivando equipo ID={}", id);

        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new EquipoNotFoundException(id));

        equipo.setEstado(Equipo.EstadoEquipo.INACTIVO);
        Equipo desactivado = equipoRepository.save(equipo);

        log.info("[team-service] Equipo desactivado. ID={}, nombre={}", desactivado.getId(), desactivado.getNombre());
        return EquipoDTO.Response.fromEntity(desactivado);
    }

    private void validarUsuarioPuedeCompetar(Long usuarioId, String rol) {
        try {
            ClientDTO.UsuarioResumen usuario = userClient.obtenerResumenUsuario(usuarioId);
            if (!usuario.isPuedeCompetar()) {
                log.warn("[team-service] {} ID={} no puede competir. Estado={}", rol, usuarioId, usuario.getEstado());
                throw new IllegalStateException(rol + " con ID=" + usuarioId
                        + " no puede competir (estado: " + usuario.getEstado() + ")");
            }
            log.info("[team-service] {} ID={} validado correctamente (nickname={})", rol, usuarioId, usuario.getNickname());
        } catch (FeignException.NotFound e) {
            throw new ServicioExternoException("user-service", "Usuario ID=" + usuarioId + " no encontrado");
        } catch (FeignException e) {
            log.error("[team-service] Error al contactar user-service para usuarioId={}: {}", usuarioId, e.getMessage());
            throw new ServicioExternoException("user-service", e.getMessage());
        }
    }

    private void validarJuegoActivo(Long juegoId) {
        try {
            ClientDTO.JuegoResumen juego = gameClient.obtenerJuego(juegoId);
            if (!"ACTIVO".equals(juego.getEstado())) {
                log.warn("[team-service] Juego ID={} no está ACTIVO. Estado={}", juegoId, juego.getEstado());
                throw new IllegalStateException("El juego '" + juego.getNombre() + "' no está activo para nuevos equipos");
            }
            log.info("[team-service] Juego ID={} validado correctamente (nombre={})", juegoId, juego.getNombre());
        } catch (FeignException.NotFound e) {
            throw new ServicioExternoException("game-service", "Juego ID=" + juegoId + " no encontrado");
        } catch (FeignException e) {
            log.error("[team-service] Error al contactar game-service para juegoId={}: {}", juegoId, e.getMessage());
            throw new ServicioExternoException("game-service", e.getMessage());
        }
    }
}
