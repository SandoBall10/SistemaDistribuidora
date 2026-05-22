package com.distribuidora.erp.interfaces.dto.proveedor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProveedorUpdateDto {

    @NotNull(message = "El tipo de persona es obligatorio")
    private Long tipoPersonaId;

    @NotNull(message = "El tipo de documento es obligatorio")
    private Long tipoDocumentoId;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 25, message = "El número de documento no debe superar 25 caracteres")
    private String numeroDocumento;

    @Size(max = 150, message = "Los nombres no deben superar 150 caracteres")
    private String nombres;

    @Size(max = 100, message = "El apellido paterno no debe superar 100 caracteres")
    private String apellidoPaterno;

    @Size(max = 100, message = "El apellido materno no debe superar 100 caracteres")
    private String apellidoMaterno;

    @Size(max = 200, message = "La razón social no debe superar 200 caracteres")
    private String razonSocial;

    @Size(max = 250, message = "La dirección no debe superar 250 caracteres")
    private String direccion;

    @Size(max = 150, message = "El email no debe superar 150 caracteres")
    private String email;

    @Size(max = 30, message = "El teléfono no debe superar 30 caracteres")
    private String telefono;

    @Size(max = 6, message = "El ubigeo no debe superar 6 caracteres")
    private String ubigeoId;

    @Size(max = 200, message = "El nombre comercial no debe superar 200 caracteres")
    private String nombreComercial;

    @Size(max = 40, message = "El estado SUNAT no debe superar 40 caracteres")
    private String estadoSunat;

    @Size(max = 40, message = "La condición SUNAT no debe superar 40 caracteres")
    private String condicionSunat;

    @Size(max = 1, message = "El género debe ser un carácter (ej. M o F)")
    private String genero;

    private Boolean esContribuyente;

    private Integer plazoCreditoDias;

    @Size(max = 40, message = "La cuenta soles no debe superar 40 caracteres")
    private String cuentaSoles;

    public Long getTipoPersonaId() { return tipoPersonaId; }
    public void setTipoPersonaId(Long tipoPersonaId) { this.tipoPersonaId = tipoPersonaId; }

    public Long getTipoDocumentoId() { return tipoDocumentoId; }
    public void setTipoDocumentoId(Long tipoDocumentoId) { this.tipoDocumentoId = tipoDocumentoId; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUbigeoId() { return ubigeoId; }
    public void setUbigeoId(String ubigeoId) { this.ubigeoId = ubigeoId; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getEstadoSunat() { return estadoSunat; }
    public void setEstadoSunat(String estadoSunat) { this.estadoSunat = estadoSunat; }

    public String getCondicionSunat() { return condicionSunat; }
    public void setCondicionSunat(String condicionSunat) { this.condicionSunat = condicionSunat; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Boolean getEsContribuyente() { return esContribuyente; }
    public void setEsContribuyente(Boolean esContribuyente) { this.esContribuyente = esContribuyente; }

    public Integer getPlazoCreditoDias() { return plazoCreditoDias; }
    public void setPlazoCreditoDias(Integer plazoCreditoDias) { this.plazoCreditoDias = plazoCreditoDias; }

    public String getCuentaSoles() { return cuentaSoles; }
    public void setCuentaSoles(String cuentaSoles) { this.cuentaSoles = cuentaSoles; }
}
