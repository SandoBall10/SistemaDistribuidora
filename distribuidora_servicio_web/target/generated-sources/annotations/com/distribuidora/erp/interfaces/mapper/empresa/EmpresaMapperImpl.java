package com.distribuidora.erp.interfaces.mapper.empresa;

import com.distribuidora.erp.domain.entity.erp.Empresa;
import com.distribuidora.erp.interfaces.dto.empresa.EmpresaResponseDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-25T08:52:36-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class EmpresaMapperImpl implements EmpresaMapper {

    @Override
    public EmpresaResponseDto toResponse(Empresa entity) {
        if ( entity == null ) {
            return null;
        }

        EmpresaResponseDto empresaResponseDto = new EmpresaResponseDto();

        empresaResponseDto.setId( entity.getId() );
        empresaResponseDto.setRuc( entity.getRuc() );
        empresaResponseDto.setRazonSocial( entity.getRazonSocial() );
        empresaResponseDto.setEmail( entity.getEmail() );
        empresaResponseDto.setTelefono( entity.getTelefono() );
        empresaResponseDto.setDireccion( entity.getDireccion() );
        empresaResponseDto.setActivo( entity.isActivo() );
        empresaResponseDto.setFechaCreacion( entity.getFechaCreacion() );
        empresaResponseDto.setFechaModificacion( entity.getFechaModificacion() );
        empresaResponseDto.setUsuarioCreacion( entity.getUsuarioCreacion() );
        empresaResponseDto.setUsuarioModificacion( entity.getUsuarioModificacion() );

        empresaResponseDto.setUbigeoCodigo( entity.getUbigeo() != null ? entity.getUbigeo().getCodigoUbigeo() : null );

        return empresaResponseDto;
    }
}
