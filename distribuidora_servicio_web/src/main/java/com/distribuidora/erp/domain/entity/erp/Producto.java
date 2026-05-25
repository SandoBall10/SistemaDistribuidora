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
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "productos", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "clase_producto_id", nullable = false)
    private Long claseProductoId;

    @Column(name = "codigo", nullable = false, length = 60)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "unidad_medida_base_id", nullable = false)
    private Long unidadMedidaBaseId;

    @Column(name = "tipo_igv_id", nullable = false)
    private Long tipoIgvId;

    @Column(name = "precio_venta", nullable = false, precision = 18, scale = 4)
    private BigDecimal precioVenta = BigDecimal.ZERO;

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
            @JoinColumn(name = "clase_producto_id", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private ClaseProducto claseProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_medida_base_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UnidadMedida unidadMedidaBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_igv_id", referencedColumnName = "id", insertable = false, updatable = false)
    private CatalogoTipoIgv tipoIgv;

    @PostLoad
    @PrePersist
    @PreUpdate
    private void normalizarPrecioVenta() {
        if (precioVenta == null) {
            precioVenta = BigDecimal.ZERO;
        }
    }
}
