package com.distribuidora.erp.domain.entity.erp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "personas", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
public class Persona {

    @Id
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "numero_documento", nullable = false, length = 25)
    private String numeroDocumento;

    // Para el login JWT, no necesitamos el resto de columnas por ahora.
}

