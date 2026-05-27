package com.esports.sanctionservice.service;

import com.esports.sanctionservice.client.TeamServiceClient;
import com.esports.sanctionservice.client.UserServiceClient;
import com.esports.sanctionservice.dto.SancionDTO;
import com.esports.sanctionservice.exception.SancionNotFoundException;
import com.esports.sanctionservice.exception.ServicioExternoException;
import com.esports.sanctionservice.model.Sancion;
import com.esports.sanctionservice.repository.SancionRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SancionService {

    private static final Logger log = LoggerFactory.getLogger(SancionService.class);

    private final SancionRepository  sancionRepository;
    private final UserServiceClient   userClient;
    private final TeamServiceClient   teamClient;

    public SancionService(SancionRepository sancionRepository,
                          UserServiceClient userClient,
                          TeamServiceClient teamClient) {
        this.sancionRepository = sancionRepository;
        this.userClient        = userClient;
        this.teamClient        = teamClient;
    }

    public SancionDTO.Response crearSancion(SancionDTO.Request request) {
        log.info("[sanction-service] Creando sanción. usuarioId={}, equipoId={}, severidad={}",
                request.getUsuarioId(), request.getEquipoId(), request.getSeveridad());

        if (request.getUsuarioId() == null && request.getEquipoId() == null) {
            throw new IllegalArgumentException("La sanción debe tener al menos un destinatario: usuarioId o equipoId");
        }

        if (!request.getFechaFin().isAfter(request.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        if (request.getUsuarioId() != null) {
            validarExistenciaUsuario(request.getUsuarioId());
        }
        if (request.getEquipoId() != null) {
            validarExistenciaEquipo(request.getEquipoId());
        }
        Sancion sancion = Sancion.builder()
                .usuarioId(request.getUsuarioId())
                .equipoId(request.getEquipoId())
                .motivo(request.getMotivo())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .severidad(request.getSeveridad())
                .estado(Sancion.EstadoSancion.ACTIVA)
                .build();

        Sancion guardada = sancionRepository.save(sancion);
        log.info("[sanction-service] Sanción creada. ID={}, severidad={}", guardada.getId(), guardada.getSeveridad());
        return SancionDTO.Response.fromEntity(guardada);
    }

    @Transactional(readOnly = true)
    public List<SancionDTO.Response> listarSanciones(Long usuarioId, Long equipoId,
                                                      Sancion.EstadoSancion estado) {
        log.info("[sanction-service] Listando sanciones. usuarioId={}, equipoId={}, estado={}",
                usuarioId, equipoId, estado);

        List<Sancion> sanciones;
        if (usuarioId != null && estado != null) {
            sanciones = sancionRepository.findByUsuarioIdAndEstado(usuarioId, estado);
        } else if (equipoId != null && estado != null) {
            sanciones = sancionRepository.findByEquipoIdAndEstado(equipoId, estado);
        } else if (usuarioId != null) {
            sanciones = sancionRepository.findByUsuarioId(usuarioId);
        } else if (equipoId != null) {
            sanciones = sancionRepository.findByEquipoId(equipoId);
        } else if (estado != null) {
            sanciones = sancionRepository.findByEstado(estado);
        } else {
            sanciones = sancionRepository.findAll();
        }

        return sanciones.stream().map(SancionDTO.Response::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public SancionDTO.Response buscarPorId(Long id) {
        log.info("[sanction-service] Buscando sanción ID={}", id);
        return SancionDTO.Response.fromEntity(obtenerSancion(id));
    }

    @Transactional(readOnly = true)
    public SancionDTO.VerificacionResponse verificarBloqueoUsuario(Long usuarioId) {
        log.info("[sanction-service] Verificando bloqueo para usuarioId={}", usuarioId);

        List<Sancion> bloqueantes = sancionRepository
                .findSancionesBloqueantesUsuario(usuarioId, LocalDateTime.now());

        if (bloqueantes.isEmpty()) {
            log.info("[sanction-service] Usuario ID={} sin sanciones bloqueantes", usuarioId);
            return SancionDTO.VerificacionResponse.noBloqueado(usuarioId, "USUARIO");
        }

        Sancion primera = bloqueantes.get(0);
        log.warn("[sanction-service] Usuario ID={} BLOQUEADO. Motivo: {}", usuarioId, primera.getMotivo());
        return SancionDTO.VerificacionResponse.bloqueado(usuarioId, "USUARIO", primera.getMotivo());
    }


    @Transactional(readOnly = true)
    public SancionDTO.VerificacionResponse verificarBloqueoEquipo(Long equipoId) {
        log.info("[sanction-service] Verificando bloqueo para equipoId={}", equipoId);

        List<Sancion> bloqueantes = sancionRepository
                .findSancionesBloqueantesEquipo(equipoId, LocalDateTime.now());

        if (bloqueantes.isEmpty()) {
            log.info("[sanction-service] Equipo ID={} sin sanciones bloqueantes", equipoId);
            return SancionDTO.VerificacionResponse.noBloqueado(equipoId, "EQUIPO");
        }

        Sancion primera = bloqueantes.get(0);
        log.warn("[sanction-service] Equipo ID={} BLOQUEADO. Motivo: {}", equipoId, primera.getMotivo());
        return SancionDTO.VerificacionResponse.bloqueado(equipoId, "EQUIPO", primera.getMotivo());
    }


    public SancionDTO.Response actualizarSancion(Long id, SancionDTO.Request request) {
        log.info("[sanction-service] Actualizando sanción ID={}", id);

        Sancion sancion = obtenerSancion(id);

        if (sancion.getEstado() == Sancion.EstadoSancion.CERRADA) {
            throw new IllegalStateException("No se puede modificar una sanción CERRADA");
        }

        if (!request.getFechaFin().isAfter(request.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        sancion.setMotivo(request.getMotivo());
        sancion.setFechaInicio(request.getFechaInicio());
        sancion.setFechaFin(request.getFechaFin());
        sancion.setSeveridad(request.getSeveridad());

        Sancion actualizada = sancionRepository.save(sancion);
        log.info("[sanction-service] Sanción actualizada. ID={}", id);
        return SancionDTO.Response.fromEntity(actualizada);
    }

    public SancionDTO.Response cerrarSancion(Long id, String justificacion) {
        log.info("[sanction-service] Cerrando sanción ID={}", id);

        Sancion sancion = obtenerSancion(id);

        if (sancion.getEstado() == Sancion.EstadoSancion.CERRADA) {
            throw new IllegalStateException("La sanción ya está CERRADA");
        }

        sancion.setEstado(Sancion.EstadoSancion.CERRADA);
        Sancion cerrada = sancionRepository.save(sancion);

        log.info("[sanction-service] Sanción cerrada. ID={}, motivo justif.: {}", id, justificacion);
        return SancionDTO.Response.fromEntity(cerrada);
    }

    public int cerrarSancionesVencidas() {
        List<Sancion> vencidas = sancionRepository.findSancionesVencidas(LocalDateTime.now());
        vencidas.forEach(s -> s.setEstado(Sancion.EstadoSancion.CERRADA));
        sancionRepository.saveAll(vencidas);
        log.info("[sanction-service] Sanciones vencidas cerradas automáticamente: {}", vencidas.size());
        return vencidas.size();
    }


    private Sancion obtenerSancion(Long id) {
        return sancionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[sanction-service] Sanción no encontrada ID={}", id);
                    return new SancionNotFoundException(id);
                });
    }

    private void validarExistenciaUsuario(Long usuarioId) {
        try {
            userClient.obtenerResumenUsuario(usuarioId);
            log.info("[sanction-service] Usuario ID={} validado", usuarioId);
        } catch (FeignException.NotFound e) {
            throw new ServicioExternoException("user-service", "Usuario ID=" + usuarioId + " no encontrado");
        } catch (FeignException e) {
            log.error("[sanction-service] Error al contactar user-service: {}", e.getMessage());
            throw new ServicioExternoException("user-service", e.getMessage());
        }
    }

    private void validarExistenciaEquipo(Long equipoId) {
        try {
            teamClient.obtenerEquipo(equipoId);
            log.info("[sanction-service] Equipo ID={} validado", equipoId);
        } catch (FeignException.NotFound e) {
            throw new ServicioExternoException("team-service", "Equipo ID=" + equipoId + " no encontrado");
        } catch (FeignException e) {
            log.error("[sanction-service] Error al contactar team-service: {}", e.getMessage());
            throw new ServicioExternoException("team-service", e.getMessage());
        }
    }
}
