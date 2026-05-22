package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByEmpresaIdAndCodigo(Long empresaId, String codigo);

    boolean existsByEmpresaIdAndId(Long empresaId, Long id);

    @EntityGraph(attributePaths = {"claseProducto", "unidadMedidaBase", "tipoIgv"})
    @NonNull Page<Producto> findByEmpresaId(@NonNull Long empresaId, @NonNull Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"claseProducto", "unidadMedidaBase", "tipoIgv"})
    @NonNull Optional<Producto> findById(@NonNull Long id);
}
