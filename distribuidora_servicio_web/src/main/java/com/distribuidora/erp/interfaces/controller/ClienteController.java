package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.application.service.ClienteService;
import com.distribuidora.erp.interfaces.dto.cliente.ClienteCreateDto;
import com.distribuidora.erp.interfaces.dto.cliente.ClienteResponseDto;
import com.distribuidora.erp.security.jwt.JwtPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_VENDEDOR')")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> listarActivos(
            @AuthenticationPrincipal JwtPrincipal principal) {
        return ResponseEntity.ok(clienteService.listarActivos(principal.empresaId()));
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDto> crear(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody ClienteCreateDto dto) {
        ClienteResponseDto created = clienteService.crear(
                dto,
                principal.empresaId(),
                String.valueOf(principal.usuarioId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
