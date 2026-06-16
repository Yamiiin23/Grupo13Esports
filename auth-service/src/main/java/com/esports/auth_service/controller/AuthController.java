package com.esports.auth_service.controller;

import com.esports.auth_service.dto.AuthDTO;
import com.esports.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticacion", description = "Controlador encargado del CRUD de cuentas, registro y login con JWT")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una cuenta con credenciales de acceso y asigna un rol inicial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo ya registrado")
    })
    public ResponseEntity<AuthDTO.TokenResponse> registrar(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        log.info("[auth-service] POST /api/v1/auth/register");
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales de un usuario y devuelve un Token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    public ResponseEntity<AuthDTO.TokenResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        log.info("[auth-service] POST /api/v1/auth/login");
        return ResponseEntity.ok(authService.login(request));
    }


    @GetMapping
    @Operation(summary = "Listar todas las cuentas", description = "Retorna un listado de todas las credenciales registradas")
    public ResponseEntity<List<AuthDTO.TokenResponse>> listarTodas() {
        log.info("[auth-service] GET /api/v1/auth");
        return ResponseEntity.ok(authService.listarCuentas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cuenta por ID", description = "Obtiene los detalles de acceso de una cuenta específica")
    public ResponseEntity<AuthDTO.TokenResponse> buscarPorId(@PathVariable Long id) {
        log.info("[auth-service] GET /api/v1/auth/{}", id);
        return ResponseEntity.ok(authService.buscarPorId(id));
    }

    @PutMapping("/{id}/rol")
    @Operation(summary = "Actualizar rol de una cuenta", description = "Permite cambiar los privilegios de un usuario")
    public ResponseEntity<AuthDTO.TokenResponse> actualizarRol(@PathVariable Long id, @RequestParam String nuevoRol) {
        log.info("[auth-service] PUT /api/v1/auth/{}/rol", id);
        return ResponseEntity.ok(authService.actualizarRol(id, nuevoRol));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar o desactivar cuenta", description = "Remueve los accesos de la plataforma")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        log.info("[auth-service] DELETE /api/v1/auth/{}", id);
        authService.eliminarOAnularCuenta(id);
        return ResponseEntity.noContent().build();
    }
}