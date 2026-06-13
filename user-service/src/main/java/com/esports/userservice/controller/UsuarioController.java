package com.esports.userservice.controller;

import com.esports.userservice.dto.UsuarioDTO;
import com.esports.userservice.model.Usuario;
import com.esports.userservice.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Módulo Usuarios", description = "Controlador interactivo para la gestión integral de jugadores, capitanes y estados de cuenta en la plataforma")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario en el sistema con su rol inicial (Jugador, Organizador, etc.) de forma persistente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta (Fallo en validaciones gramaticales de los campos)")
    })
    public ResponseEntity<UsuarioDTO.Response> crearUsuario(
            @Valid @RequestBody UsuarioDTO.Request request) {

        log.info("[user-service] POST /api/v1/usuarios - nickname={}", request.getNickname());
        UsuarioDTO.Response response = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar y filtrar usuarios", description = "Recupera todos los usuarios de la plataforma permitiendo aplicar filtros opcionales por Rol y Estado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de usuarios recuperada correctamente")
    })
    public ResponseEntity<List<UsuarioDTO.Response>> listarUsuarios(
            @Parameter(description = "Filtrar por rol del usuario (ej. JUGADOR, ADMINISTRADOR)", example = "JUGADOR")
            @RequestParam(required = false) Usuario.RolUsuario rol,
            @Parameter(description = "Filtrar por estado operativo (ej. ACTIVO, INACTIVO)", example = "ACTIVO")
            @RequestParam(required = false) Usuario.EstadoUsuario estado) {

        log.info("[user-service] GET /api/v1/usuarios - rol={}, estado={}", rol, estado);
        List<UsuarioDTO.Response> response = usuarioService.listarUsuarios(rol, estado);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Obtiene los detalles estructurados de un usuario específico. Endpoint consumido síncronamente por team-service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario localizado de forma exitosa",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class))),
            @ApiResponse(responseCode = "404", description = "El identificador provisto no corresponde a ningún usuario registrado")
    })
    public ResponseEntity<UsuarioDTO.Response> buscarPorId(
            @Parameter(description = "ID único incremental del usuario", example = "1", required = true)
            @PathVariable Long id) {
        log.info("[user-service] GET /api/v1/usuarios/{}", id);
        UsuarioDTO.Response response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/resumen")
    @Operation(summary = "Obtener resumen del usuario", description = "Retorna un perfil condensado con los datos clave del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumen generado correctamente",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.ResumenResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no localizado")
    })
    public ResponseEntity<UsuarioDTO.ResumenResponse> obtenerResumen(
            @Parameter(description = "ID único del usuario", example = "1", required = true)
            @PathVariable Long id) {
        log.info("[user-service] GET /api/v1/usuarios/{}/resumen", id);
        UsuarioDTO.ResumenResponse response = usuarioService.obtenerResumen(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar perfil de usuario", description = "Modifica las propiedades generales del usuario basándose en su ID único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos actualizados exitosamente",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "Errores de validación en la carga de datos"),
            @ApiResponse(responseCode = "404", description = "El usuario a modificar no existe")
    })
    public ResponseEntity<UsuarioDTO.Response> actualizarUsuario(
            @Parameter(description = "ID del usuario a modificar", example = "1", required = true) @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.Request request) {

        log.info("[user-service] PUT /api/v1/usuarios/{}", id);
        UsuarioDTO.Response response = usuarioService.actualizarUsuario(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del usuario", description = "Cambia de forma específica el estado operativo del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "Payload de estado inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioDTO.Response> actualizarEstado(
            @Parameter(description = "ID del usuario", example = "1", required = true) @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.EstadoRequest request) {

        log.info("[user-service] PATCH /api/v1/usuarios/{}/estado - nuevoEstado={}", id, request.getEstado());
        UsuarioDTO.Response response = usuarioService.actualizarEstado(id, request.getEstado());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un usuario", description = "Aplica una desactivación o baja al perfil del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario dado de baja de manera conforme"),
            @ApiResponse(responseCode = "404", description = "El ID enviado no pertenece a ningún usuario")
    })
    public ResponseEntity<UsuarioDTO.Response> desactivarUsuario(
            @Parameter(description = "ID del usuario a remover", example = "1", required = true)
            @PathVariable Long id) {
        log.info("[user-service] DELETE /api/v1/usuarios/{}", id);
        UsuarioDTO.Response response = usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok(response);
    }
}