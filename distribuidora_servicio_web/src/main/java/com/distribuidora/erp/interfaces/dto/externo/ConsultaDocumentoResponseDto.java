package com.distribuidora.erp.interfaces.dto.externo;

/**
 * Respuesta unificada para el front al consultar DNI/RUC vía backend.
 */
public class ConsultaDocumentoResponseDto {

    /** "DNI" o "RUC". */
    private String tipo;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String razonSocial;
    private String nombreComercial;
    private String direccion;
    private String ubigeoCodigo;
    private String departamento;
    private String provincia;
    private String distrito;
    private String estadoSunat;
    private String condicionSunat;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getUbigeoCodigo() { return ubigeoCodigo; }
    public void setUbigeoCodigo(String ubigeoCodigo) { this.ubigeoCodigo = ubigeoCodigo; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    public String getEstadoSunat() { return estadoSunat; }
    public void setEstadoSunat(String estadoSunat) { this.estadoSunat = estadoSunat; }

    public String getCondicionSunat() { return condicionSunat; }
    public void setCondicionSunat(String condicionSunat) { this.condicionSunat = condicionSunat; }
}
