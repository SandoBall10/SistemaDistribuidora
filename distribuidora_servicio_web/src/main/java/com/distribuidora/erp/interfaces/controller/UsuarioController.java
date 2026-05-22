package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.application.service.UsuarioService;
import com.distribuidora.erp.interfaces.dto.usuario.UsuarioCreateDto;
import com.distribuidora.erp.interfaces.dto.usuario.UsuarioResponseDto;
import com.distribuidora.erp.interfaces.dto.usuario.UsuarioUpdateDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDto>> list(@NonNull Pageable pageable) {
        return ResponseEntity.ok(usuarioService.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getById(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> create(@Valid @RequestBody UsuarioCreateDto dto) {
        UsuarioResponseDto created = usuarioService.create(dto, "system");
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> update(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDto dto) {
        return ResponseEntity.ok(usuarioService.update(id, dto, "system"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.softDelete(id, "system");
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-estado")
    public ResponseEntity<UsuarioResponseDto> toggleEstado(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.toggleEstado(id, "system"));
    }
}
