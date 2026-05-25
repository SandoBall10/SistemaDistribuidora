package com.distribuidora.erp.interfaces.dto.venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO completo para comprobante / ticket de venta (cabecera + detalle + cliente).
 */
public class VentaComprobanteDto {

    private Long id;
    private Long empresaId;
    private Long clienteId;
    private String clienteCodigo;
    private String clienteNombre;
    private String clienteNumeroDocumento;
    private LocalDate fechaEmision;
    private OffsetDateTime fechaCreacion;
    private String tipoComprobanteCodigo;
    private String serie;
    private String numeroComprobante;
    private String monedaCodigo;
    private BigDecimal totalGravado;
    private BigDecimal totalIgv;
    private BigDecimal totalVenta;
    private List<VentaDetalleResponseDto> detalles = new ArrayList<>();

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

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteCodigo() {
        return clienteCodigo;
    }

    public void setClienteCodigo(String clienteCodigo) {
        this.clienteCodigo = clienteCodigo;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteNumeroDocumento() {
        return clienteNumeroDocumento;
    }

    public void setClienteNumeroDocumento(String clienteNumeroDocumento) {
        this.clienteNumeroDocumento = clienteNumeroDocumento;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getTipoComprobanteCodigo() {
        return tipoComprobanteCodigo;
    }

    public void setTipoComprobanteCodigo(String tipoComprobanteCodigo) {
        this.tipoComprobanteCodigo = tipoComprobanteCodigo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
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

    public BigDecimal getTotalGravado() {
        return totalGravado;
    }

    public void setTotalGravado(BigDecimal totalGravado) {
        this.totalGravado = totalGravado;
    }

    public BigDecimal getTotalIgv() {
        return totalIgv;
    }

    public void setTotalIgv(BigDecimal totalIgv) {
        this.totalIgv = totalIgv;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }

    public List<VentaDetalleResponseDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<VentaDetalleResponseDto> detalles) {
        this.detalles = detalles;
    }
}
