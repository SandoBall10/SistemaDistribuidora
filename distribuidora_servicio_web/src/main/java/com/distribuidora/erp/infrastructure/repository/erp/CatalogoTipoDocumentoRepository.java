package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.CatalogoTipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogoTipoDocumentoRepository extends JpaRepository<CatalogoTipoDocumento, Long> {

    List<CatalogoTipoDocumento> findByActivoIsTrueOrderByNombreAsc();
}
