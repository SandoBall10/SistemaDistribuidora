package com.distribuidora.erp.interfaces.dto.producto;

import java.math.BigDecimal;

public class ProductoResponseDto {
    private Long id;
    private Long empresaId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Long claseProductoId;
    private String claseProductoNombre;
    private Long unidadMedidaBaseId;
    private String unidadMedidaNombre;
    private Long tipoIgvId;
    private String tipoIgvCodigo;
    private String tipoIgvNombre;
    private BigDecimal tipoIgvPorcentaje;
    private BigDecimal precioVenta;
    private boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getClaseProductoNombre() { return claseProductoNombre; }
    public void setClaseProductoNombre(String claseProductoNombre) { this.claseProductoNombre = claseProductoNombre; }
    public Long getUnidadMedidaBaseId() { return unidadMedidaBaseId; }
    public void setUnidadMedidaBaseId(Long unidadMedidaBaseId) { this.unidadMedidaBaseId = unidadMedidaBaseId; }
    public String getUnidadMedidaNombre() { return unidadMedidaNombre; }
    public void setUnidadMedidaNombre(String unidadMedidaNombre) { this.unidadMedidaNombre = unidadMedidaNombre; }
    public Long getTipoIgvId() { return tipoIgvId; }
    public void setTipoIgvId(Long tipoIgvId) { this.tipoIgvId = tipoIgvId; }
    public String getTipoIgvCodigo() { return tipoIgvCodigo; }
    public void setTipoIgvCodigo(String tipoIgvCodigo) { this.tipoIgvCodigo = tipoIgvCodigo; }
    public String getTipoIgvNombre() { return tipoIgvNombre; }
    public void setTipoIgvNombre(String tipoIgvNombre) { this.tipoIgvNombre = tipoIgvNombre; }
    public BigDecimal getTipoIgvPorcentaje() { return tipoIgvPorcentaje; }
    public void setTipoIgvPorcentaje(BigDecimal tipoIgvPorcentaje) { this.tipoIgvPorcentaje = tipoIgvPorcentaje; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
