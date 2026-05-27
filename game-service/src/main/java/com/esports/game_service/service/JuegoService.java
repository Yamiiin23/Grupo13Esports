package com.esports.game_service.service;


import com.esports.game_service.dto.JuegoDTO;
import com.esports.game_service.exception.JuegoNotFoundException;
import com.esports.game_service.exception.JuegoYaExisteException;
import com.esports.game_service.model.Juego;
import com.esports.game_service.repository.JuegoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JuegoService {

    private static final Logger log = LoggerFactory.getLogger(JuegoService.class);
    private final JuegoRepository juegoRepository;

    public JuegoService(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    public JuegoDTO.Response crearJuego(JuegoDTO.Request request) {
        log.info("[game-service] Intentando crear juego: {}", request.getNombre());

        if (juegoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            log.warn("[game-service] Nombre duplicado detectado: {}", request.getNombre());
            throw new JuegoYaExisteException(request.getNombre());
        }

        Juego juego = Juego.builder()
                .nombre(request.getNombre())
                .genero(request.getGenero())
                .modalidad(request.getModalidad())
                .jugadoresPorEquipo(request.getJugadoresPorEquipo())
                .estado(Juego.EstadoJuego.ACTIVO)
                .build();

        Juego guardado = juegoRepository.save(juego);
        log.info("[game-service] Juego creado exitosamente. ID={}, nombre={}", guardado.getId(), guardado.getNombre());

        return JuegoDTO.Response.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public List<JuegoDTO.Response> listarJuegos(boolean soloActivos) {
        log.info("[game-service] Listando juegos. soloActivos={}", soloActivos);

        List<Juego> juegos = soloActivos
                ? juegoRepository.findByEstado(Juego.EstadoJuego.ACTIVO)
                : juegoRepository.findAll();

        return juegos.stream()
                .map(JuegoDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public JuegoDTO.Response buscarPorId(Long id) {
        log.info("[game-service] Buscando juego por ID={}", id);

        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[game-service] Juego no encontrado. ID={}", id);
                    return new JuegoNotFoundException(id);
                });

        return JuegoDTO.Response.fromEntity(juego);
    }

    public JuegoDTO.Response actualizarJuego(Long id, JuegoDTO.Request request) {
        log.info("[game-service] Actualizando juego ID={}", id);

        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new JuegoNotFoundException(id));
        if (!juego.getNombre().equalsIgnoreCase(request.getNombre())
                && juegoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            log.warn("[game-service] Actualización fallida - nombre duplicado: {}", request.getNombre());
            throw new JuegoYaExisteException(request.getNombre());
        }

        juego.setNombre(request.getNombre());
        juego.setGenero(request.getGenero());
        juego.setModalidad(request.getModalidad());
        juego.setJugadoresPorEquipo(request.getJugadoresPorEquipo());

        Juego actualizado = juegoRepository.save(juego);
        log.info("[game-service] Juego actualizado exitosamente. ID={}", actualizado.getId());

        return JuegoDTO.Response.fromEntity(actualizado);
    }

    public JuegoDTO.Response desactivarJuego(Long id) {
        log.info("[game-service] Desactivando juego ID={}", id);

        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new JuegoNotFoundException(id));

        if (juego.getEstado() == Juego.EstadoJuego.INACTIVO) {
            log.warn("[game-service] El juego ID={} ya estaba inactivo", id);
        }

        juego.setEstado(Juego.EstadoJuego.INACTIVO);
        Juego desactivado = juegoRepository.save(juego);

        log.info("[game-service] Juego desactivado. ID={}, nombre={}", desactivado.getId(), desactivado.getNombre());
        return JuegoDTO.Response.fromEntity(desactivado);
    }
}

