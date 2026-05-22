package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.application.service.ConsultaExternaService;
import com.distribuidora.erp.interfaces.dto.externo.ConsultaDocumentoResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/externo")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR') or hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDEDOR')")
public class ConsultaExternaController {

    private final ConsultaExternaService consultaExternaService;

    public ConsultaExternaController(ConsultaExternaService consultaExternaService) {
        this.consultaExternaService = consultaExternaService;
    }

    /**
     * Proxifica apis.net.pe. Errores traducidos: 400 para validación o fallo del proveedor (p. ej. token),
     * 404 si el documento no existe / no válido ante SUNAT-RENIEC.
     */
    @GetMapping("/consultar-documento")
    public ResponseEntity<ConsultaDocumentoResponseDto> consultarDocumento(
            @RequestParam String tipo,
            @RequestParam String numero
    ) {
        return ResponseEntity.ok(consultaExternaService.consultarDocumento(tipo, numero));
    }
}
