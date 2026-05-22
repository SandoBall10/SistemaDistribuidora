package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {
    List<UnidadMedida> findByActivoTrueOrderByNombreAsc();
}
