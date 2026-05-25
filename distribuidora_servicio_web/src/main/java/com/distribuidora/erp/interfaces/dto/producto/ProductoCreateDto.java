package com.distribuidora.erp.interfaces.dto.producto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductoCreateDto {
    @NotNull(message = "La empresa es obligatoria")
    private Long empresaId;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 60, message = "El código no debe superar 60 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no debe superar 200 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La clase de producto es obligatoria")
    private Long claseProductoId;

    @NotNull(message = "La unidad de medida base es obligatoria")
    private Long unidadMedidaBaseId;

    @NotNull(message = "El tipo de afectación IGV es obligatorio")
    private Long tipoIgvId;

    @DecimalMin(value = "0", message = "El precio de venta no puede ser negativo")
    private BigDecimal precioVenta;

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Long getClaseProductoId() { return claseProductoId; }
    public void setClaseProductoId(Long claseProductoId) { this.claseProductoId = claseProductoId; }
    public Long getUnidadMedidaBaseId() { return unidadMedidaBaseId; }
    public void setUnidadMedidaBaseId(Long unidadMedidaBaseId) { this.unidadMedidaBaseId = unidadMedidaBaseId; }
    public Long getTipoIgvId() { return tipoIgvId; }
    public void setTipoIgvId(Long tipoIgvId) { this.tipoIgvId = tipoIgvId; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
}
