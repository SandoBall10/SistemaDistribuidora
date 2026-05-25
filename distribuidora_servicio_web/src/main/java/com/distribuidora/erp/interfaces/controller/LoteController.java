package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.application.service.LoteService;
import com.distribuidora.erp.interfaces.dto.lote.LoteDisponibleDto;
import com.distribuidora.erp.security.jwt.JwtPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_VENDEDOR')")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<LoteDisponibleDto>> listarDisponibles(
            @AuthenticationPrincipal JwtPrincipal principal,
            @RequestParam Long productoId) {
        return ResponseEntity.ok(loteService.listarDisponibles(principal.empresaId(), productoId));
    }
}
