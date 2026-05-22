package com.distribuidora.erp.interfaces.dto.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClienteCreateDto {

    /** DNI o RUC (se resuelve a catálogo SUNAT en servidor). */
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "DNI|RUC", message = "tipoDocumento debe ser DNI o RUC")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 25, message = "El número de documento no debe superar 25 caracteres")
    private String numeroDocumento;

    @NotBlank(message = "El nombre o razón social es obligatorio")
    @Size(max = 200, message = "El nombre no debe superar 200 caracteres")
    private String razonSocialNombre;

    @Size(max = 250, message = "La dirección no debe superar 250 caracteres")
    private String direccion;

    @Size(max = 150)
    private String nombres;

    @Size(max = 100)
    private String apellidoPaterno;

    @Size(max = 100)
    private String apellidoMaterno;

    @Size(max = 40)
    private String estadoSunat;

    @Size(max = 40)
    private String condicionSunat;

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getRazonSocialNombre() {
        return razonSocialNombre;
    }

    public void setRazonSocialNombre(String razonSocialNombre) {
        this.razonSocialNombre = razonSocialNombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getEstadoSunat() {
        return estadoSunat;
    }

    public void setEstadoSunat(String estadoSunat) {
        this.estadoSunat = estadoSunat;
    }

    public String getCondicionSunat() {
        return condicionSunat;
    }

    public void setCondicionSunat(String condicionSunat) {
        this.condicionSunat = condicionSunat;
    }
}
