package com.distribuidora.erp.domain.entity.erp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "proveedores", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "persona_id", nullable = false)
    private Long personaId;

    @Column(name = "codigo_proveedor", nullable = false, length = 30)
    private String codigoProveedor;

    @Column(name = "plazo_credito_dias")
    private Integer plazoCreditoDias;

    @Column(name = "cuenta_soles", length = 40)
    private String cuentaSoles;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "empresa_id", referencedColumnName = "empresa_id", insertable = false, updatable = false),
            @JoinColumn(name = "persona_id", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private Persona persona;
}
