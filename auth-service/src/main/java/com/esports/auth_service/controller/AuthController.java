package com.esports.auth_service.controller;


import com.esports.auth_service.dto.AuthDTO;
import com.esports.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
// hola
@RestController
@RequestMapping("/api/v1/auth")
//cambio guia 1
@Tag(name = "Autenticacion", description = "Controlador encargado del registro de usuarios y generación de tokens jwt")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    //cambio guia 1
    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una cuenta con credenciales de acceso y asigna un rol inicial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente y token jwt generado",
                content = @Content(schema = @Schema(implementation = AuthDTO.TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o correo ya registrado", content = @Content)
    })
    public ResponseEntity<AuthDTO.TokenResponse> registrar(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        log.info("[auth-service] POST /api/v1/auth/register");
        AuthDTO.TokenResponse response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    //cambio guia 1
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales de un usuario y devuelve un Token JWT valido por 24 horas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticacion exitosa, retorna el token Bearer",
            content = @Content(schema = @Schema(implementation = AuthDTO.TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = ("Credenciales incorrectas (Email o contraseña errónea"), content = @Content),
            @ApiResponse(responseCode = "400", description = "Formato de solicitud incorrecto", content = @Content)
    })
    public ResponseEntity<AuthDTO.TokenResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        log.info("[auth-service] POST /api/v1/auth/login");
        AuthDTO.TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}