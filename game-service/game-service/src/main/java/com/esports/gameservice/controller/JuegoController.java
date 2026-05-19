package com.esports.gameservice.controller;

import com.esports.gameservice.dto.JuegoDTO;
import com.esports.gameservice.service.JuegoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/juegos")
public class JuegoController {

    private static final Logger log = LoggerFactory.getLogger(JuegoController.class);

    private final JuegoService juegoService;

    public JuegoController(JuegoService juegoService) {
        this.juegoService = juegoService;
    }

    @PostMapping
    public ResponseEntity<JuegoDTO.Response> crearJuego(
            @Valid @RequestBody JuegoDTO.Request request) {

        log.info("[game-service] POST /api/v1/juegos - nombre={}", request.getNombre());
        JuegoDTO.Response response = juegoService.crearJuego(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<JuegoDTO.Response>> listarJuegos(
            @RequestParam(defaultValue = "false") boolean soloActivos) {

        log.info("[game-service] GET /api/v1/juegos - soloActivos={}", soloActivos);
        List<JuegoDTO.Response> response = juegoService.listarJuegos(soloActivos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JuegoDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[game-service] GET /api/v1/juegos/{}", id);
        JuegoDTO.Response response = juegoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JuegoDTO.Response> actualizarJuego(
            @PathVariable Long id,
            @Valid @RequestBody JuegoDTO.Request request) {

        log.info("[game-service] PUT /api/v1/juegos/{}", id);
        JuegoDTO.Response response = juegoService.actualizarJuego(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JuegoDTO.Response> desactivarJuego(@PathVariable Long id) {
        log.info("[game-service] DELETE /api/v1/juegos/{}", id);
        JuegoDTO.Response response = juegoService.desactivarJuego(id);
        return ResponseEntity.ok(response);
    }
}
