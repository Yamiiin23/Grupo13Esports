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
        log.info("Registrando juego: {}", request.getNombre());

        if (juegoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new JuegoYaExisteException(request.getNombre());
        }

        Juego juego = Juego.builder()
                .nombre(request.getNombre())
                .genero(request.getGenero())
                .modalidad(request.getModalidad())
                .jugadoresPorEquipo(request.getJugadoresPorEquipo())
                .estado(Juego.EstadoJuego.ACTIVO)
                .build();

        return JuegoDTO.Response.fromEntity(juegoRepository.save(juego));
    }

    @Transactional(readOnly = true)
    public List<JuegoDTO.Response> listarJuegos(boolean soloActivos) {
        List<Juego> juegos = soloActivos
                ? juegoRepository.findByEstado(Juego.EstadoJuego.ACTIVO)
                : juegoRepository.findAll();

        return juegos.stream()
                .map(JuegoDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public JuegoDTO.Response buscarPorId(Long id) {
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new JuegoNotFoundException(id));
        return JuegoDTO.Response.fromEntity(juego);
    }

    public JuegoDTO.Response actualizarJuego(Long id, JuegoDTO.Request request) {
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new JuegoNotFoundException(id));

        if (!juego.getNombre().equalsIgnoreCase(request.getNombre())
                && juegoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new JuegoYaExisteException(request.getNombre());
        }

        juego.setNombre(request.getNombre());
        juego.setGenero(request.getGenero());
        juego.setModalidad(request.getModalidad());
        juego.setJugadoresPorEquipo(request.getJugadoresPorEquipo());

        return JuegoDTO.Response.fromEntity(juegoRepository.save(juego));
    }

    public JuegoDTO.Response desactivarJuego(Long id) {
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new JuegoNotFoundException(id));

        juego.setEstado(Juego.EstadoJuego.INACTIVO);
        return JuegoDTO.Response.fromEntity(juegoRepository.save(juego));
    }
}