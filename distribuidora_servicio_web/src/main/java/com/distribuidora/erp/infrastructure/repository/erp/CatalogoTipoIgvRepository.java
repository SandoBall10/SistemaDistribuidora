package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.CatalogoTipoIgv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogoTipoIgvRepository extends JpaRepository<CatalogoTipoIgv, Long> {
    List<CatalogoTipoIgv> findByActivoTrueOrderByNombreAsc();
}
