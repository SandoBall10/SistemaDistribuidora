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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "tipo_comprobante_codigo", nullable = false, length = 20)
    private String tipoComprobanteCodigo;

    @Column(name = "serie", nullable = false, length = 10)
    private String serie;

    @Column(name = "numero_comprobante", nullable = false, length = 20)
    private String numeroComprobante;

    @Column(name = "moneda_codigo", nullable = false, length = 6)
    private String monedaCodigo;

    @Column(name = "total_gravado", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalGravado;

    @Column(name = "total_igv", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalIgv;

    @Column(name = "total_venta", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalVenta;

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
            @JoinColumn(name = "cliente_id", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private Cliente cliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();
}
