package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.application.service.ProductoService;
import com.distribuidora.erp.interfaces.dto.producto.CatalogoOptionDto;
import com.distribuidora.erp.interfaces.dto.producto.IgvCatalogoItemDto;
import com.distribuidora.erp.interfaces.dto.producto.ProductoCreateDto;
import com.distribuidora.erp.interfaces.dto.producto.ProductoResponseDto;
import com.distribuidora.erp.interfaces.dto.producto.ProductoUpdateDto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_VENDEDOR')")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductoResponseDto>> list(
            @RequestParam @NonNull Long empresaId,
            @NonNull Pageable pageable) {
        return ResponseEntity.ok(productoService.listarPaginado(empresaId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDto> create(@Valid @RequestBody ProductoCreateDto dto) {
        ProductoResponseDto created = productoService.crear(dto, "system");
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> update(@PathVariable Long id, @Valid @RequestBody ProductoUpdateDto dto) {
        return ResponseEntity.ok(productoService.actualizar(id, dto, "system"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.eliminarLogico(id, "system");
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-estado")
    public ResponseEntity<ProductoResponseDto> toggleEstado(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.toggleEstado(id, "system"));
    }

    @GetMapping("/catalogos/clases")
    public ResponseEntity<List<CatalogoOptionDto>> listClases(@RequestParam Long empresaId) {
        return ResponseEntity.ok(productoService.listarClasesProducto(empresaId));
    }

    @GetMapping("/catalogos/unidades")
    public ResponseEntity<List<CatalogoOptionDto>> listUnidades() {
        return ResponseEntity.ok(productoService.listarUnidadesMedida());
    }

    @GetMapping("/catalogos/igv")
    public ResponseEntity<List<IgvCatalogoItemDto>> listIgv() {
        return ResponseEntity.ok(productoService.listarIgv());
    }
}
