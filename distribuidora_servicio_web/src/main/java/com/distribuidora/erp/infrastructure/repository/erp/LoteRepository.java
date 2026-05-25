package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    Optional<Lote> findByEmpresaIdAndProductoIdAndCodigoLote(Long empresaId, Long productoId, String codigoLote);

    @Query("""
            SELECT l FROM Lote l
            WHERE l.empresaId = :empresaId
              AND l.productoId = :productoId
              AND UPPER(l.codigoLote) = UPPER(:codigoLote)
            """)
    Optional<Lote> findByEmpresaIdAndProductoIdAndCodigoLoteIgnoreCase(
            @Param("empresaId") Long empresaId,
            @Param("productoId") Long productoId,
            @Param("codigoLote") String codigoLote);

    @Query("""
            SELECT l FROM Lote l
            WHERE l.empresaId = :empresaId
              AND l.productoId = :productoId
              AND l.activo = true
              AND l.stockActual > 0
            ORDER BY l.codigoLote ASC
            """)
    List<Lote> findDisponiblesByEmpresaIdAndProductoId(
            @Param("empresaId") Long empresaId,
            @Param("productoId") Long productoId);

    /**
     * Descuenta stock sin cargar la asociación {@code producto} (evita dirty checking en catálogo).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Lote l
            SET l.stockActual = l.stockActual - :cantidad,
                l.referenciaDocumento = :referencia,
                l.fechaModificacion = :fechaModificacion,
                l.usuarioModificacion = :usuarioModificacion
            WHERE l.empresaId = :empresaId
              AND l.productoId = :productoId
              AND UPPER(l.codigoLote) = UPPER(:codigoLote)
              AND l.stockActual >= :cantidad
            """)
    int descontarStock(
            @Param("empresaId") Long empresaId,
            @Param("productoId") Long productoId,
            @Param("codigoLote") String codigoLote,
            @Param("cantidad") BigDecimal cantidad,
            @Param("referencia") String referencia,
            @Param("fechaModificacion") OffsetDateTime fechaModificacion,
            @Param("usuarioModificacion") String usuarioModificacion);
}
