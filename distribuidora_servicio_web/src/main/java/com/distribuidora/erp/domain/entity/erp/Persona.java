package com.distribuidora.erp.domain.entity.erp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "personas", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "numero_documento", nullable = false, length = 25)
    private String numeroDocumento;

    @Column(name = "tipo_persona_id", nullable = false)
    private Long tipoPersonaId;

    @Column(name = "tipo_documento_id", nullable = false)
    private Long tipoDocumentoId;

    @Column(name = "razon_social_nombre", nullable = false, length = 200)
    private String razonSocialNombre;

    @Column(name = "apellido_paterno", length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 100)
    private String apellidoMaterno;

    @Column(name = "nombres", length = 150)
    private String nombres;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "direccion", length = 250)
    private String direccion;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "estado_sunat", length = 40)
    private String estadoSunat;

    @Column(name = "condicion_sunat", length = 40)
    private String condicionSunat;

    /** M / F u otro según catálogo (opcional). */
    @Column(name = "genero", length = 1)
    private String genero;

    @Column(name = "es_contribuyente", nullable = false)
    private boolean esContribuyente = false;

    /** Código UBIGEO SUNAT de 6 caracteres (opcional). */
    @Column(name = "ubigeo_codigo", length = 6)
    private String ubigeoCodigo;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "usuario_creacion", length = 64)
    private String usuarioCreacion;

    @Column(name = "fecha_modificacion", nullable = false)
    private OffsetDateTime fechaModificacion;

    @Column(name = "usuario_modificacion", length = 64)
    private String usuarioModificacion;
}
