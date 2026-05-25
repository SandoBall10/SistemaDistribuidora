package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.application.service.VentaService;
import com.distribuidora.erp.interfaces.dto.venta.VentaComprobanteDto;
import com.distribuidora.erp.interfaces.dto.venta.VentaCreateDto;
import com.distribuidora.erp.interfaces.dto.venta.VentaResponseDto;
import com.distribuidora.erp.security.jwt.JwtPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_VENDEDOR')")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public ResponseEntity<List<VentaResponseDto>> listar(@AuthenticationPrincipal JwtPrincipal principal) {
        return ResponseEntity.ok(ventaService.listarVentas(principal.empresaId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaComprobanteDto> obtener(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerComprobante(principal.empresaId(), id));
    }

    @PostMapping
    public ResponseEntity<VentaResponseDto> crear(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody VentaCreateDto dto) {
        VentaResponseDto created = ventaService.registrarVenta(
                dto,
                principal.empresaId(),
                String.valueOf(principal.usuarioId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
