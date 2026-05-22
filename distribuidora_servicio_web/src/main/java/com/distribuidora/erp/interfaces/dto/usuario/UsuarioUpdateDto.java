package com.distribuidora.erp.interfaces.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioUpdateDto {

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener 8 dígitos")
    private String numeroDocumento;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 120, message = "Los nombres no deben superar 120 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 120, message = "Los apellidos no deben superar 120 caracteres")
    private String apellidos;

    @NotBlank(message = "El rol es obligatorio")
    @Size(max = 50, message = "El código de rol no debe superar 50 caracteres")
    private String rolCodigo;

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getRolCodigo() { return rolCodigo; }
    public void setRolCodigo(String rolCodigo) { this.rolCodigo = rolCodigo; }
}
