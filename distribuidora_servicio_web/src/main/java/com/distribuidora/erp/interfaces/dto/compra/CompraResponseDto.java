package com.distribuidora.erp.interfaces.dto.compra;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class CompraResponseDto {

    private Long id;
    private Long empresaId;
    private Long proveedorId;
    private LocalDate fechaIngreso;
    private String almacen;
    private String tipoComprobanteCodigo;
    private String numeroComprobante;
    private String monedaCodigo;
    private OffsetDateTime fechaCreacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    public String getTipoComprobanteCodigo() {
        return tipoComprobanteCodigo;
    }

    public void setTipoComprobanteCodigo(String tipoComprobanteCodigo) {
        this.tipoComprobanteCodigo = tipoComprobanteCodigo;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getMonedaCodigo() {
        return monedaCodigo;
    }

    public void setMonedaCodigo(String monedaCodigo) {
        this.monedaCodigo = monedaCodigo;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
