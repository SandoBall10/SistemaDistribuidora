package com.distribuidora.erp.interfaces.dto.producto;

import java.math.BigDecimal;

public class IgvCatalogoItemDto {
    private Long id;
    private String nombre;
    private BigDecimal porcentaje;

    public IgvCatalogoItemDto() {
    }

    public IgvCatalogoItemDto(Long id, String nombre, BigDecimal porcentaje) {
        this.id = id;
        this.nombre = nombre;
        this.porcentaje = porcentaje;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }
}
