package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByEmpresaIdOrderByFechaIngresoDesc(Long empresaId);

    Optional<Compra> findByIdAndEmpresaId(Long id, Long empresaId);
}
