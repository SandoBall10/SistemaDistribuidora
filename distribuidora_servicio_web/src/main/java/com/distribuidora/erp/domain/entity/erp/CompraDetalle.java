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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "compras_detalle", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
public class CompraDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "empresa_id", referencedColumnName = "empresa_id"),
            @JoinColumn(name = "compra_id", referencedColumnName = "id")
    })
    private Compra compra;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "cantidad", nullable = false, precision = 18, scale = 4)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal precioUnitario;

    @Column(name = "lote_codigo", nullable = false, length = 60)
    private String loteCodigo;

    @Column(name = "fecha_produccion")
    private LocalDate fechaProduccion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

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
            @JoinColumn(name = "producto_id", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private Producto producto;
}
