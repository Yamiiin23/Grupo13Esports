package com.esports.userservice.controller;

import com.esports.userservice.dto.UsuarioDTO;
import com.esports.userservice.model.Usuario;
import com.esports.userservice.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO.Response> crearUsuario(
            @Valid @RequestBody UsuarioDTO.Request request) {

        log.info("[user-service] POST /api/v1/usuarios - nickname={}", request.getNickname());
        UsuarioDTO.Response response = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO.Response>> listarUsuarios(
            @RequestParam(required = false) Usuario.RolUsuario rol,
            @RequestParam(required = false) Usuario.EstadoUsuario estado) {

        log.info("[user-service] GET /api/v1/usuarios - rol={}, estado={}", rol, estado);
        List<UsuarioDTO.Response> response = usuarioService.listarUsuarios(rol, estado);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("[user-service] GET /api/v1/usuarios/{}", id);
        UsuarioDTO.Response response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/resumen")
    public ResponseEntity<UsuarioDTO.ResumenResponse> obtenerResumen(@PathVariable Long id) {
        log.info("[user-service] GET /api/v1/usuarios/{}/resumen", id);
        UsuarioDTO.ResumenResponse response = usuarioService.obtenerResumen(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO.Response> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.Request request) {

        log.info("[user-service] PUT /api/v1/usuarios/{}", id);
        UsuarioDTO.Response response = usuarioService.actualizarUsuario(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioDTO.Response> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.EstadoRequest request) {

        log.info("[user-service] PATCH /api/v1/usuarios/{}/estado - nuevoEstado={}", id, request.getEstado());
        UsuarioDTO.Response response = usuarioService.actualizarEstado(id, request.getEstado());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDTO.Response> desactivarUsuario(@PathVariable Long id) {
        log.info("[user-service] DELETE /api/v1/usuarios/{}", id);
        UsuarioDTO.Response response = usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok(response);
    }
}
