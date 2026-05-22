package com.distribuidora.erp.interfaces.dto.compra;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class CompraCreateDto {

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;

    @Size(max = 150, message = "Almacén no debe superar 150 caracteres")
    private String almacen;

    @NotBlank(message = "El tipo de comprobante es obligatorio")
    @Size(max = 20, message = "Tipo de comprobante no válido")
    private String tipoComprobanteCodigo;

    @NotBlank(message = "El número de comprobante es obligatorio")
    @Size(max = 80, message = "Número de comprobante demasiado largo")
    private String numeroComprobante;

    @NotBlank(message = "La moneda es obligatoria")
    @Size(max = 6, message = "Código de moneda no válido")
    private String monedaCodigo;

    @NotEmpty(message = "Debe incluir al menos un detalle de producto")
    @Valid
    private List<CompraDetalleCreateDto> detalles;

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

    public List<CompraDetalleCreateDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<CompraDetalleCreateDto> detalles) {
        this.detalles = detalles;
    }
}
