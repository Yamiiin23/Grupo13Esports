package com.esports.auth_service.controller;


import com.esports.auth_service.dto.AuthDTO;
import com.esports.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDTO.TokenResponse> registrar(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        log.info("[auth-service] POST /api/v1/auth/register");
        AuthDTO.TokenResponse response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.TokenResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        log.info("[auth-service] POST /api/v1/auth/login");
        AuthDTO.TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}