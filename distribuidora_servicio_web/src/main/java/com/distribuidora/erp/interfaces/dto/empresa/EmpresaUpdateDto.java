package com.distribuidora.erp.interfaces.dto.empresa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class EmpresaUpdateDto {

    @Size(min = 11, max = 15)
    private String ruc;

    @Size(max = 200)
    private String razonSocial;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 30)
    private String telefono;

    @Size(max = 250)
    private String direccion;

    @Size(min = 6, max = 6)
    private String ubigeoCodigo;

    private Boolean activo;

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getUbigeoCodigo() { return ubigeoCodigo; }
    public void setUbigeoCodigo(String ubigeoCodigo) { this.ubigeoCodigo = ubigeoCodigo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}

