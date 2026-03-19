package com.distribuidora.erp.domain.entity.erp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "catalogo_ubigeo", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
public class CatalogoUbigeo {

    @Id
    @Column(name = "codigo_ubigeo", length = 6, nullable = false)
    private String codigoUbigeo;

    @Column(name = "distrito", nullable = false, length = 120)
    private String distrito;

    @Column(name = "provincia", nullable = false, length = 120)
    private String provincia;

    @Column(name = "departamento", nullable = false, length = 120)
    private String departamento;
}

