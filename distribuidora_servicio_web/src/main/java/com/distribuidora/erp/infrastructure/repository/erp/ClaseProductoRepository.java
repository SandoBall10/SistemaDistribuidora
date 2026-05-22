package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.ClaseProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaseProductoRepository extends JpaRepository<ClaseProducto, Long> {
    List<ClaseProducto> findByEmpresaIdAndActivoTrueOrderByNombreAsc(Long empresaId);

    Optional<ClaseProducto> findByIdAndEmpresaId(Long id, Long empresaId);
}
