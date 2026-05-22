package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = {"rol"})
    Optional<Usuario> findByEmpresaIdAndNombreUsuarioAndActivoTrue(Long empresaId, String nombreUsuario);

    Optional<Usuario> findByEmpresaIdAndNombreUsuario(Long empresaId, String nombreUsuario);

    @Override
    @EntityGraph(attributePaths = {"rol", "persona"})
    @NonNull Page<Usuario> findAll(@NonNull Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"rol", "persona"})
    @NonNull Optional<Usuario> findById(@NonNull Long id);
}

