package com.distribuidora.erp.interfaces.dto.proveedor;

public class ProveedorResponseDto {

    private Long id;
    private Long empresaId;
    private Long personaId;
    private String codigoProveedor;

    /** 1 Natural, 2 Jurídica. */
    private Long tipoPersonaId;

    private Long tipoDocumentoId;
    private String tipoDocumentoNombre;
    private String numeroDocumento;

    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String razonSocial;

    /** Concatenación / razón según tabla personas (consulta rápida en listados). */
    private String razonSocialNombre;

    private String direccion;
    private String email;
    private String telefono;

    private String nombreComercial;
    private String estadoSunat;
    private String condicionSunat;
    private String genero;
    private Boolean esContribuyente;

    private String ubigeoId;
    private Integer plazoCreditoDias;
    private String cuentaSoles;
    private boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public Long getPersonaId() { return personaId; }
    public void setPersonaId(Long personaId) { this.personaId = personaId; }

    public String getCodigoProveedor() { return codigoProveedor; }
    public void setCodigoProveedor(String codigoProveedor) { this.codigoProveedor = codigoProveedor; }

    public Long getTipoPersonaId() { return tipoPersonaId; }
    public void setTipoPersonaId(Long tipoPersonaId) { this.tipoPersonaId = tipoPersonaId; }

    public Long getTipoDocumentoId() { return tipoDocumentoId; }
    public void setTipoDocumentoId(Long tipoDocumentoId) { this.tipoDocumentoId = tipoDocumentoId; }

    public String getTipoDocumentoNombre() { return tipoDocumentoNombre; }
    public void setTipoDocumentoNombre(String tipoDocumentoNombre) { this.tipoDocumentoNombre = tipoDocumentoNombre; }

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

    public String getRazonSocialNombre() { return razonSocialNombre; }
    public void setRazonSocialNombre(String razonSocialNombre) { this.razonSocialNombre = razonSocialNombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

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

    public String getUbigeoId() { return ubigeoId; }
    public void setUbigeoId(String ubigeoId) { this.ubigeoId = ubigeoId; }

    public Integer getPlazoCreditoDias() { return plazoCreditoDias; }
    public void setPlazoCreditoDias(Integer plazoCreditoDias) { this.plazoCreditoDias = plazoCreditoDias; }

    public String getCuentaSoles() { return cuentaSoles; }
    public void setCuentaSoles(String cuentaSoles) { this.cuentaSoles = cuentaSoles; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
