package com.distribuidora.erp.interfaces.dto.proveedor;

public class TipoDocumentoCatalogoDto {

    private Long id;
    private String nombre;
    private String codigoSunat;

    public TipoDocumentoCatalogoDto() {
    }

    public TipoDocumentoCatalogoDto(Long id, String nombre, String codigoSunat) {
        this.id = id;
        this.nombre = nombre;
        this.codigoSunat = codigoSunat;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoSunat() { return codigoSunat; }
    public void setCodigoSunat(String codigoSunat) { this.codigoSunat = codigoSunat; }
}
