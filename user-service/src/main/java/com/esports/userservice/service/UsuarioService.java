package com.esports.userservice.service;

import com.esports.userservice.dto.UsuarioDTO;
import com.esports.userservice.exception.UsuarioDuplicadoException;
import com.esports.userservice.exception.UsuarioNotFoundException;
import com.esports.userservice.model.Usuario;
import com.esports.userservice.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioDTO.Response crearUsuario(UsuarioDTO.Request request) {
        log.info("[user-service] Creando usuario - nickname={}, email={}", request.getNickname(), request.getEmail());

        if (usuarioRepository.existsByNicknameIgnoreCase(request.getNickname())) {
            log.warn("[user-service] Nickname duplicado: {}", request.getNickname());
            throw new UsuarioDuplicadoException("nickname", request.getNickname());
        }

        if (usuarioRepository.existsByEmailIgnoreCase(request.getEmail())) {
            log.warn("[user-service] Email duplicado: {}", request.getEmail());
            throw new UsuarioDuplicadoException("email", request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .rol(request.getRol())
                .estado(Usuario.EstadoUsuario.ACTIVO)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("[user-service] Usuario creado exitosamente. ID={}, nickname={}", guardado.getId(), guardado.getNickname());

        return UsuarioDTO.Response.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> listarUsuarios(Usuario.RolUsuario rol, Usuario.EstadoUsuario estado) {
        log.info("[user-service] Listando usuarios - rol={}, estado={}", rol, estado);

        List<Usuario> usuarios;

        if (rol != null && estado != null) {
            usuarios = usuarioRepository.findByRolAndEstado(rol, estado);
        } else if (rol != null) {
            usuarios = usuarioRepository.findByRol(rol);
        } else if (estado != null) {
            usuarios = usuarioRepository.findByEstado(estado);
        } else {
            usuarios = usuarioRepository.findAll();
        }

        return usuarios.stream().map(UsuarioDTO.Response::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO.Response buscarPorId(Long id) {
        log.info("[user-service] Buscando usuario ID={}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[user-service] Usuario no encontrado. ID={}", id);
                    return new UsuarioNotFoundException(id);
                });

        return UsuarioDTO.Response.fromEntity(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO.ResumenResponse obtenerResumen(Long id) {
        log.info("[user-service] Obteniendo resumen de usuario ID={}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        return UsuarioDTO.ResumenResponse.fromEntity(usuario);
    }

    public UsuarioDTO.Response actualizarUsuario(Long id, UsuarioDTO.Request request) {
        log.info("[user-service] Actualizando usuario ID={}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        if (!usuario.getNickname().equalsIgnoreCase(request.getNickname())
                && usuarioRepository.existsByNicknameIgnoreCaseAndIdNot(request.getNickname(), id)) {
            log.warn("[user-service] Actualización fallida - nickname duplicado: {}", request.getNickname());
            throw new UsuarioDuplicadoException("nickname", request.getNickname());
        }

        if (!usuario.getEmail().equalsIgnoreCase(request.getEmail())
                && usuarioRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), id)) {
            log.warn("[user-service] Actualización fallida - email duplicado: {}", request.getEmail());
            throw new UsuarioDuplicadoException("email", request.getEmail());
        }

        usuario.setNombre(request.getNombre());
        usuario.setNickname(request.getNickname());
        usuario.setEmail(request.getEmail());
        usuario.setRol(request.getRol());

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("[user-service] Usuario actualizado. ID={}, nickname={}", actualizado.getId(), actualizado.getNickname());

        return UsuarioDTO.Response.fromEntity(actualizado);
    }

    public UsuarioDTO.Response actualizarEstado(Long id, Usuario.EstadoUsuario nuevoEstado) {
        log.info("[user-service] Cambiando estado de usuario ID={} a {}", id, nuevoEstado);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        Usuario.EstadoUsuario estadoAnterior = usuario.getEstado();
        usuario.setEstado(nuevoEstado);

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("[user-service] Estado actualizado. ID={}, {} → {}", id, estadoAnterior, nuevoEstado);

        return UsuarioDTO.Response.fromEntity(actualizado);
    }

    public UsuarioDTO.Response desactivarUsuario(Long id) {
        log.info("[user-service] Desactivando usuario ID={}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        if (usuario.getEstado() == Usuario.EstadoUsuario.INACTIVO) {
            log.warn("[user-service] El usuario ID={} ya estaba inactivo", id);
        }

        usuario.setEstado(Usuario.EstadoUsuario.INACTIVO);
        Usuario desactivado = usuarioRepository.save(usuario);

        log.info("[user-service] Usuario desactivado. ID={}, nickname={}", desactivado.getId(), desactivado.getNickname());
        return UsuarioDTO.Response.fromEntity(desactivado);
    }
}
