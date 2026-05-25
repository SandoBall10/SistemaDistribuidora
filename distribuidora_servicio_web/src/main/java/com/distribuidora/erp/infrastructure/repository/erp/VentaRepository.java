package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.Venta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @EntityGraph(attributePaths = "detalles")
    @Query("SELECT v FROM Venta v WHERE v.id = :id AND v.empresaId = :empresaId")
    Optional<Venta> findComprobanteByIdAndEmpresaId(@Param("id") Long id, @Param("empresaId") Long empresaId);

    List<Venta> findByEmpresaIdOrderByFechaCreacionDesc(Long empresaId);
}
