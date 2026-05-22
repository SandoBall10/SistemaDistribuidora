package com.distribuidora.erp.infrastructure.repository.erp;

import com.distribuidora.erp.domain.entity.erp.CatalogoUbigeo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CatalogoUbigeoRepository extends JpaRepository<CatalogoUbigeo, String> {

    @Query("select distinct u.departamento from CatalogoUbigeo u order by u.departamento")
    List<String> findDistinctDepartamentos();

    @Query("select distinct u.provincia from CatalogoUbigeo u where u.departamento = ?1 order by u.provincia")
    List<String> findDistinctProvinciasByDepartamento(String departamento);

    List<CatalogoUbigeo> findByDepartamentoAndProvinciaOrderByDistrito(String departamento, String provincia);
}

