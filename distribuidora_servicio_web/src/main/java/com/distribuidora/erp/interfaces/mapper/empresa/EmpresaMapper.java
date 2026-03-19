package com.distribuidora.erp.interfaces.mapper.empresa;

import com.distribuidora.erp.domain.entity.erp.Empresa;
import com.distribuidora.erp.interfaces.dto.empresa.EmpresaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    @Mapping(target = "ubigeoCodigo", expression = "java(entity.getUbigeo() != null ? entity.getUbigeo().getCodigoUbigeo() : null)")
    EmpresaResponseDto toResponse(Empresa entity);
}

