package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    Optional<Lote> findByEmpresaIdAndProductoIdAndCodigoLote(Long empresaId, Long productoId, String codigoLote);
}
