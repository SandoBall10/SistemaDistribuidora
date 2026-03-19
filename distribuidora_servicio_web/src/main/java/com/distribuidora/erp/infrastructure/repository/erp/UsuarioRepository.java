package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmpresaIdAndNombreUsuarioAndActivoTrue(Long empresaId, String nombreUsuario);

    Optional<Usuario> findByEmpresaIdAndNombreUsuario(Long empresaId, String nombreUsuario);
}

