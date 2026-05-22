package com.distribuidora.erp.domain.entity.erp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "proveedor_id", nullable = false)
    private Long proveedorId;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "almacen", length = 150)
    private String almacen;

    @Column(name = "tipo_comprobante_codigo", nullable = false, length = 20)
    private String tipoComprobanteCodigo;

    @Column(name = "numero_comprobante", nullable = false, length = 80)
    private String numeroComprobante;

    @Column(name = "moneda_codigo", nullable = false, length = 6)
    private String monedaCodigo;

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
            @JoinColumn(name = "proveedor_id", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private Proveedor proveedor;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompraDetalle> detalles = new ArrayList<>();
}
