package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.application.service.ProveedorService;
import com.distribuidora.erp.interfaces.dto.proveedor.ProveedorCreateDto;
import com.distribuidora.erp.interfaces.dto.proveedor.ProveedorResponseDto;
import com.distribuidora.erp.interfaces.dto.proveedor.ProveedorUpdateDto;
import com.distribuidora.erp.interfaces.dto.proveedor.TipoDocumentoCatalogoDto;
import com.distribuidora.erp.interfaces.dto.proveedor.UbigeoDistritoDto;
import com.distribuidora.erp.interfaces.dto.proveedor.UbigeoUbicacionDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;

import org.springframework.http.HttpStatus;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public ResponseEntity<Page<ProveedorResponseDto>> list(
            @RequestParam @NonNull Long empresaId,
            @NonNull Pageable pageable
    ) {
        return ResponseEntity.ok(proveedorService.listarPaginado(empresaId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProveedorResponseDto> create(@Valid @RequestBody ProveedorCreateDto dto) {
        ProveedorResponseDto created = proveedorService.crear(dto, "system");
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorUpdateDto dto
    ) {
        return ResponseEntity.ok(proveedorService.actualizar(id, dto, "system"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proveedorService.eliminarLogico(id, "system");
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-estado")
    public ResponseEntity<ProveedorResponseDto> toggleEstado(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.toggleEstado(id, "system"));
    }

    @GetMapping("/catalogos/tipos-documento")
    public ResponseEntity<List<TipoDocumentoCatalogoDto>> listTiposDocumento() {
        return ResponseEntity.ok(proveedorService.listarTiposDocumento());
    }

    @GetMapping("/catalogos/ubigeo/departamentos")
    public ResponseEntity<List<String>> listUbigeoDepartamentos() {
        return ResponseEntity.ok(proveedorService.listarDepartamentosUbigeo());
    }

    @GetMapping("/catalogos/ubigeo/provincias")
    public ResponseEntity<List<String>> listUbigeoProvincias(@RequestParam String departamento) {
        return ResponseEntity.ok(proveedorService.listarProvinciasUbigeo(departamento));
    }

    @GetMapping("/catalogos/ubigeo/distritos")
    public ResponseEntity<List<UbigeoDistritoDto>> listUbigeoDistritos(
            @RequestParam String departamento,
            @RequestParam String provincia
    ) {
        return ResponseEntity.ok(proveedorService.listarDistritosUbigeo(departamento, provincia));
    }

    @GetMapping("/catalogos/ubigeo/resumen")
    public ResponseEntity<UbigeoUbicacionDto> getUbigeoResumen(@RequestParam String codigo) {
        Optional<UbigeoUbicacionDto> ubic = proveedorService.buscarUbigeoPorCodigo(codigo);
        if (ubic.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ubic.get());
    }
}
