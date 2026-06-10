package com.esports.sanctionservice.service;

import com.esports.sanctionservice.client.TeamServiceClient;
import com.esports.sanctionservice.client.UserServiceClient;
import com.esports.sanctionservice.dto.SancionDTO;
import com.esports.sanctionservice.exception.SancionNotFoundException;
import com.esports.sanctionservice.exception.SancionValidationException;
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

    public SancionService(SancionRepository sancionRepository, UserServiceClient userClient, TeamServiceClient teamClient) {
        this.sancionRepository = sancionRepository;
        this.userClient        = userClient;
        this.teamClient        = teamClient;
    }

    public SancionDTO.Response crearSancion(SancionDTO.Request request) {
        log.info("[sanction-service] Creando sanción. usuarioId={}, equipoId={}, severidad={}", request.getUsuarioId(), request.getEquipoId(), request.getSeveridad());

        if (request.getUsuarioId() == null && request.getEquipoId() == null) {
            throw new SancionValidationException("La sanción debe tener al menos un destinatario: usuarioId o equipoId");
        }
        if (!request.getFechaFin().isAfter(request.getFechaInicio())) {
            throw new SancionValidationException("La fecha de fin debe ser posterior a la fecha de inicio");
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

        return SancionDTO.Response.fromEntity(sancionRepository.save(sancion));
    }

    @Transactional(readOnly = true)
    public List<SancionDTO.Response> listarSanciones(Long usuarioId, Long equipoId, Sancion.EstadoSancion estado) {
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
        return SancionDTO.Response.fromEntity(obtenerSancion(id));
    }

    @Transactional(readOnly = true)
    public SancionDTO.VerificacionResponse verificarBloqueoUsuario(Long usuarioId) {
        List<Sancion> bloqueantes = sancionRepository.findSancionesBloqueantesUsuario(usuarioId, LocalDateTime.now());
        if (bloqueantes.isEmpty()) {
            return SancionDTO.VerificacionResponse.noBloqueado(usuarioId, "USUARIO");
        }
        Sancion primera = bloqueantes.get(0);
        return SancionDTO.VerificacionResponse.bloqueado(usuarioId, "USUARIO", primera.getMotivo());
    }

    @Transactional(readOnly = true)
    public SancionDTO.VerificacionResponse verificarBloqueoEquipo(Long equipoId) {
        List<Sancion> bloqueantes = sancionRepository.findSancionesBloqueantesEquipo(equipoId, LocalDateTime.now());
        if (bloqueantes.isEmpty()) {
            return SancionDTO.VerificacionResponse.noBloqueado(equipoId, "EQUIPO");
        }
        Sancion primera = bloqueantes.get(0);
        return SancionDTO.VerificacionResponse.bloqueado(equipoId, "EQUIPO", primera.getMotivo());
    }

    public SancionDTO.Response actualizarSancion(Long id, SancionDTO.Request request) {
        Sancion sancion = obtenerSancion(id);

        if (sancion.getEstado() == Sancion.EstadoSancion.CERRADA) {
            throw new SancionValidationException("No se puede modificar una sanción CERRADA");
        }
        if (sancion.getEstado() == Sancion.EstadoSancion.ANULADA) {
            throw new SancionValidationException("No se puede modificar una sanción que ha sido ANULADA");
        }
        if (!request.getFechaFin().isAfter(request.getFechaInicio())) {
            throw new SancionValidationException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        sancion.setMotivo(request.getMotivo());
        sancion.setFechaInicio(request.getFechaInicio());
        sancion.setFechaFin(request.getFechaFin());
        sancion.setSeveridad(request.getSeveridad());

        return SancionDTO.Response.fromEntity(sancionRepository.save(sancion));
    }

    public SancionDTO.Response cerrarSancion(Long id, String justificacion) {
        Sancion sancion = obtenerSancion(id);

        if (sancion.getEstado() == Sancion.EstadoSancion.CERRADA) {
            throw new SancionValidationException("La sanción ya está CERRADA");
        }
        if (sancion.getEstado() == Sancion.EstadoSancion.ANULADA) {
            throw new SancionValidationException("No se puede cerrar una sanción que fue previamente ANULADA");
        }

        sancion.setEstado(Sancion.EstadoSancion.CERRADA);
        sancion.setMotivo(sancion.getMotivo() + " [Cierre Justificado]: " + justificacion);
        return SancionDTO.Response.fromEntity(sancionRepository.save(sancion));
    }

    public int cerrarSancionesVencidas() {
        List<Sancion> vencidas = sancionRepository.findSancionesVencidas(LocalDateTime.now());
        vencidas.forEach(s -> s.setEstado(Sancion.EstadoSancion.CERRADA));
        sancionRepository.saveAll(vencidas);
        return vencidas.size();
    }

    public SancionDTO.Response anularSancionLogica(Long id, String justificacion) {
        if (justificacion == null || justificacion.isBlank()) {
            throw new SancionValidationException("La justificación de anulación es obligatoria.");
        }
        Sancion sancion = obtenerSancion(id);
        if (sancion.getEstado() == Sancion.EstadoSancion.ANULADA) {
            throw new SancionValidationException("La sanción ya se encuentra en estado ANULADA.");
        }

        sancion.setEstado(Sancion.EstadoSancion.ANULADA);
        sancion.setMotivo("[ANULACIÓN LÓGICA via DELETE]: " + justificacion + " | Motivo original: " + sancion.getMotivo());

        log.info("[sanction-service] Sanción ID={} ANULADA lógicamente.", id);
        return SancionDTO.Response.fromEntity(sancionRepository.save(sancion));
    }

    private Sancion obtenerSancion(Long id) {
        return sancionRepository.findById(id)
                .orElseThrow(() -> new SancionNotFoundException(id));
    }

    private void validarExistenciaUsuario(Long usuarioId) {
        try {
            userClient.obtenerResumenUsuario(usuarioId);
        } catch (FeignException.NotFound e) {
            throw new SancionValidationException("El usuario con ID " + usuarioId + " no existe en el sistema.");
        } catch (FeignException e) {
            log.error("[sanction-service] Caída de red al contactar user-service: {}", e.getMessage());
            throw new ServicioExternoException("user-service", "El microservicio de usuarios no responde. Intente más tarde.");
        }
    }

    private void validarExistenciaEquipo(Long equipoId) {
        try {
            teamClient.obtenerEquipo(equipoId);
        } catch (FeignException.NotFound e) {
            throw new SancionValidationException("El equipo con ID " + equipoId + " no existe en el sistema.");
        } catch (FeignException e) {
            log.error("[sanction-service] Caída de red al contactar team-service: {}", e.getMessage());
            throw new ServicioExternoException("team-service", "El microservicio de equipos no responde. Intente más tarde.");
        }
    }
}