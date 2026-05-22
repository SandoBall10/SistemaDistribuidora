package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.application.service.CompraService;
import com.distribuidora.erp.interfaces.dto.compra.CompraCreateDto;
import com.distribuidora.erp.interfaces.dto.compra.CompraResponseDto;
import com.distribuidora.erp.security.jwt.JwtPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@PreAuthorize("hasRole('ADMIN')")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<CompraResponseDto> crear(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody CompraCreateDto dto) {
        CompraResponseDto created = compraService.registrarCompra(
                dto,
                principal.empresaId(),
                String.valueOf(principal.usuarioId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CompraResponseDto>> listar(
            @AuthenticationPrincipal JwtPrincipal principal) {
        return ResponseEntity.ok(
                compraService.listarCompras(principal.empresaId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDto> obtener(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(
                compraService.obtenerCompra(principal.empresaId(), id));
    }
}
