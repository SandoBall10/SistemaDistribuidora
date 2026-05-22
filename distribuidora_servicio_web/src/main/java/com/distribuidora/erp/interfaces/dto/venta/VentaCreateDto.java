package com.distribuidora.erp.interfaces.dto.venta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VentaCreateDto {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate fechaEmision;

    @NotBlank(message = "El tipo de comprobante es obligatorio")
    @Size(max = 20, message = "Tipo de comprobante no válido")
    private String tipoComprobanteCodigo;

    @NotBlank(message = "La serie es obligatoria")
    @Size(max = 10, message = "Serie no válida")
    private String serie;

    @NotBlank(message = "El número de comprobante es obligatorio")
    @Size(max = 20, message = "Número de comprobante no válido")
    private String numeroComprobante;

    @NotBlank(message = "La moneda es obligatoria")
    @Size(max = 6, message = "Código de moneda no válido")
    private String monedaCodigo;

    /** Opcional: el backend recalcula y valida si se envían. */
    private BigDecimal totalGravado;
    private BigDecimal totalIgv;
    private BigDecimal totalVenta;

    @NotEmpty(message = "Debe incluir al menos un detalle")
    @Valid
    private List<VentaDetalleCreateDto> detalles;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
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

    public List<VentaDetalleCreateDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<VentaDetalleCreateDto> detalles) {
        this.detalles = detalles;
    }
}
