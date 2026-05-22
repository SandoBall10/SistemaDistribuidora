package com.distribuidora.erp.interfaces.dto.usuario;

public class UsuarioResponseDto {
    private Long id;
    private Long empresaId;
    private String nombreUsuario;
    private String rolCodigo;
    private String personaNombreCompleto;
    private String tipoDocumento;
    private String numeroDocumento;
    private boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getRolCodigo() { return rolCodigo; }
    public void setRolCodigo(String rolCodigo) { this.rolCodigo = rolCodigo; }

    public String getPersonaNombreCompleto() { return personaNombreCompleto; }
    public void setPersonaNombreCompleto(String personaNombreCompleto) { this.personaNombreCompleto = personaNombreCompleto; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
