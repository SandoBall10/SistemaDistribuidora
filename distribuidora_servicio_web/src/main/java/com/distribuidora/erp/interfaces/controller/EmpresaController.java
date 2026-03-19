package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.application.service.EmpresaService;
import com.distribuidora.erp.interfaces.dto.empresa.EmpresaCreateDto;
import com.distribuidora.erp.interfaces.dto.empresa.EmpresaResponseDto;
import com.distribuidora.erp.interfaces.dto.empresa.EmpresaUpdateDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    public ResponseEntity<EmpresaResponseDto> create(
            @Valid @RequestBody EmpresaCreateDto dto) {
        EmpresaResponseDto created = empresaService.create(dto, "system");
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<EmpresaResponseDto>> list(
            @RequestParam(name = "activo", required = false) Boolean activo,
            Pageable pageable) {
        return ResponseEntity.ok(empresaService.list(activo, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaUpdateDto dto) {
        return ResponseEntity.ok(empresaService.update(id, dto, "system"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        empresaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

