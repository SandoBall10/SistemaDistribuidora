package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    boolean existsByEmpresaIdAndPersonaId(Long empresaId, Long personaId);

    boolean existsByEmpresaIdAndCodigoProveedor(Long empresaId, String codigoProveedor);

    @EntityGraph(attributePaths = "persona")
    @NonNull Page<Proveedor> findByEmpresaId(@NonNull Long empresaId, @NonNull Pageable pageable);

    @EntityGraph(attributePaths = "persona")
    @NonNull Optional<Proveedor> findById(@NonNull Long id);

    @EntityGraph(attributePaths = "persona")
    Optional<Proveedor> findByEmpresaIdAndId(Long empresaId, Long id);
}
