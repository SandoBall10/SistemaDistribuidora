package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.application.service.AuthService;
import com.distribuidora.erp.interfaces.dto.auth.LoginRequestDto;
import com.distribuidora.erp.interfaces.dto.auth.RefreshRequestDto;
import com.distribuidora.erp.interfaces.dto.auth.TokenResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@Valid @RequestBody RefreshRequestDto dto) {
        return ResponseEntity.ok(authService.refresh(dto));
    }
}

