package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("""
            SELECT c FROM Cliente c
            LEFT JOIN FETCH c.persona
            WHERE c.empresaId = :empresaId AND c.activo = true
            ORDER BY c.codigoCliente ASC
            """)
    List<Cliente> findByEmpresaIdAndActivoTrueOrderByCodigoClienteAsc(@Param("empresaId") Long empresaId);

    @Query("""
            SELECT c FROM Cliente c
            LEFT JOIN FETCH c.persona
            WHERE c.empresaId = :empresaId AND c.id = :id
            """)
    Optional<Cliente> findByEmpresaIdAndId(@Param("empresaId") Long empresaId, @Param("id") Long id);

    boolean existsByEmpresaIdAndPersonaId(Long empresaId, Long personaId);

    boolean existsByEmpresaIdAndCodigoCliente(Long empresaId, String codigoCliente);
}
