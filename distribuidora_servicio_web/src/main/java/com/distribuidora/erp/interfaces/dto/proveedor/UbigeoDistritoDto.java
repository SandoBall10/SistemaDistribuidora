package com.distribuidora.erp.interfaces.dto.proveedor;

public class UbigeoDistritoDto {

    private String codigoUbigeo;
    private String nombre;

    public UbigeoDistritoDto() {
    }

    public UbigeoDistritoDto(String codigoUbigeo, String nombre) {
        this.codigoUbigeo = codigoUbigeo;
        this.nombre = nombre;
    }

    public String getCodigoUbigeo() { return codigoUbigeo; }
    public void setCodigoUbigeo(String codigoUbigeo) { this.codigoUbigeo = codigoUbigeo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
