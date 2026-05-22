package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findByEmpresaIdAndNumeroDocumento(Long empresaId, String numeroDocumento);

    Optional<Persona> findByEmpresaIdAndTipoDocumentoIdAndNumeroDocumento(
            Long empresaId,
            Long tipoDocumentoId,
            String numeroDocumento);
}
