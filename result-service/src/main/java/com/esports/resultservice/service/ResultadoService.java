package com.esports.resultservice.service;

import com.esports.resultservice.client.MatchClient;
import com.esports.resultservice.dto.ResultadoDTO;
import com.esports.resultservice.exception.ResultadoNotFoundException;
import com.esports.resultservice.model.Resultado;
import com.esports.resultservice.repository.ResultadoRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResultadoService {

    private static final Logger log = LoggerFactory.getLogger(ResultadoService.class);

    private final ResultadoRepository resultadoRepository;
    private final MatchClient matchClient;

    public ResultadoService(ResultadoRepository resultadoRepository,
                            MatchClient matchClient) {
        this.resultadoRepository = resultadoRepository;
        this.matchClient         = matchClient;
    }


    public ResultadoDTO.Response crearResultado(ResultadoDTO.Request request) {
        log.info("[result-service] Registrando resultado. partidaId={}, ganadorId={}",
                request.getPartidaId(), request.getGanadorId());

        MatchClient.PartidaResumen partida = obtenerPartidaValida(request.getPartidaId());

        if (resultadoRepository.existsByPartidaId(request.getPartidaId())) {
            throw new IllegalStateException(
                "Ya existe un resultado registrado para la partida ID=" + request.getPartidaId());
        }

        validarGanador(request.getGanadorId(), partida);

        Resultado resultado = Resultado.builder()
                .partidaId(request.getPartidaId())
                .ganadorId(request.getGanadorId())
                .puntajeA(request.getPuntajeA())
                .puntajeB(request.getPuntajeB())
                .observaciones(request.getObservaciones())
                .estadoValidacion(Resultado.EstadoValidacion.PENDIENTE)
                .build();

        Resultado guardado = resultadoRepository.save(resultado);
        log.info("[result-service] Resultado registrado. ID={}, partidaId={}", guardado.getId(), guardado.getPartidaId());
        return ResultadoDTO.Response.fromEntity(guardado);
    }


    @Transactional(readOnly = true)
    public List<ResultadoDTO.Response> listarResultados(Resultado.EstadoValidacion estado) {
        log.info("[result-service] Listando resultados. estado={}", estado);
        List<Resultado> resultados = estado != null
                ? resultadoRepository.findByEstadoValidacion(estado)
                : resultadoRepository.findAll();
        return resultados.stream().map(ResultadoDTO.Response::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ResultadoDTO.Response buscarPorId(Long id) {
        log.info("[result-service] Buscando resultado ID={}", id);
        return ResultadoDTO.Response.fromEntity(obtenerResultado(id));
    }

    @Transactional(readOnly = true)
    public ResultadoDTO.Response buscarPorPartida(Long partidaId) {
        log.info("[result-service] Buscando resultado por partidaId={}", partidaId);
        return ResultadoDTO.Response.fromEntity(
            resultadoRepository.findByPartidaId(partidaId)
                .orElseThrow(() -> new ResultadoNotFoundException(partidaId))
        );
    }

    public ResultadoDTO.Response actualizarResultado(Long id, ResultadoDTO.Request request) {
        log.info("[result-service] Actualizando resultado ID={}", id);

        Resultado resultado = obtenerResultado(id);

        if (resultado.estaValidado()) {
            log.warn("[result-service] Intento de modificar resultado VALIDADO. ID={}", id);
            throw new IllegalStateException(
                "El resultado ya fue VALIDADO y no puede modificarse sin autorización del organizador");
        }

        if (resultado.estaAnulado()) {
            throw new IllegalStateException("No se puede modificar un resultado ANULADO");
        }

        if (!resultado.getGanadorId().equals(request.getGanadorId())) {
            MatchClient.PartidaResumen partida = obtenerPartidaValida(resultado.getPartidaId());
            validarGanador(request.getGanadorId(), partida);
        }

        resultado.setGanadorId(request.getGanadorId());
        resultado.setPuntajeA(request.getPuntajeA());
        resultado.setPuntajeB(request.getPuntajeB());
        resultado.setObservaciones(request.getObservaciones());

        Resultado actualizado = resultadoRepository.save(resultado);
        log.info("[result-service] Resultado actualizado. ID={}", id);
        return ResultadoDTO.Response.fromEntity(actualizado);
    }

    public ResultadoDTO.Response cambiarValidacion(Long id, ResultadoDTO.ValidacionRequest request) {
        log.info("[result-service] Cambiando validación resultado ID={} → {}", id, request.getEstadoValidacion());

        Resultado resultado = obtenerResultado(id);

        if (resultado.estaValidado() && request.getEstadoValidacion() != Resultado.EstadoValidacion.ANULADO) {
            throw new IllegalStateException("El resultado ya está VALIDADO. Solo puede ser ANULADO.");
        }

        if (resultado.estaAnulado()) {
            throw new IllegalStateException("El resultado ya está ANULADO");
        }

        if (request.getEstadoValidacion() == Resultado.EstadoValidacion.ANULADO
                && (request.getObservaciones() == null || request.getObservaciones().isBlank())) {
            throw new IllegalArgumentException("La anulación requiere una justificación en observaciones");
        }

        resultado.setEstadoValidacion(request.getEstadoValidacion());
        if (request.getObservaciones() != null) {
            resultado.setObservaciones(request.getObservaciones());
        }

        Resultado actualizado = resultadoRepository.save(resultado);

        if (actualizado.estaValidado()) {
            finalizarPartida(actualizado.getPartidaId());
        }

        log.info("[result-service] Validación cambiada. ID={}, estado={}", id, actualizado.getEstadoValidacion());
        return ResultadoDTO.Response.fromEntity(actualizado);
    }


    private Resultado obtenerResultado(Long id) {
        return resultadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[result-service] Resultado no encontrado. ID={}", id);
                    return new ResultadoNotFoundException(id);
                });
    }

    private MatchClient.PartidaResumen obtenerPartidaValida(Long partidaId) {
        try {
            MatchClient.PartidaResumen partida = matchClient.obtenerPartida(partidaId);
            if (!"EN_CURSO".equals(partida.getEstado()) && !"FINALIZADA".equals(partida.getEstado())) {
                throw new IllegalStateException(
                    "Solo se pueden registrar resultados para partidas EN_CURSO o FINALIZADA. " +
                    "Estado actual: " + partida.getEstado());
            }
            log.info("[result-service] Partida validada. ID={}, estado={}", partidaId, partida.getEstado());
            return partida;
        } catch (FeignException.NotFound e) {
            throw new IllegalStateException("Partida ID=" + partidaId + " no encontrada en match-service");
        } catch (FeignException e) {
            log.error("[result-service] Error al contactar match-service: {}", e.getMessage());
            throw new IllegalStateException("No se pudo validar la partida");
        }
    }

    private void validarGanador(Long ganadorId, MatchClient.PartidaResumen partida) {
        boolean esParticipante = ganadorId.equals(partida.getParticipanteAId())
                              || ganadorId.equals(partida.getParticipanteBId());
        if (!esParticipante) {
            log.warn("[result-service] Ganador ID={} no es participante de la partida ID={}",
                    ganadorId, partida.getId());
            throw new IllegalArgumentException(
                "El ganador ID=" + ganadorId + " no es participante de esta partida. " +
                "Participantes válidos: " + partida.getParticipanteAId() + " y " + partida.getParticipanteBId());
        }
    }

    private void finalizarPartida(Long partidaId) {
        try {
            matchClient.cambiarEstadoPartida(partidaId,
                new MatchClient.EstadoRequest("FINALIZADA", "Resultado validado por result-service"));
            log.info("[result-service] Partida ID={} marcada como FINALIZADA en match-service", partidaId);
        } catch (FeignException e) {
            log.warn("[result-service] No se pudo finalizar partida ID={} en match-service: {}", partidaId, e.getMessage());
        }
    }
}
