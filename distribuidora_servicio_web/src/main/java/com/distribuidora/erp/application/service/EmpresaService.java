package com.distribuidora.erp.application.service;

import com.distribuidora.erp.domain.entity.erp.CatalogoUbigeo;
import com.distribuidora.erp.domain.entity.erp.Empresa;
import com.distribuidora.erp.infrastructure.repository.erp.CatalogoUbigeoRepository;
import com.distribuidora.erp.infrastructure.repository.erp.EmpresaRepository;
import com.distribuidora.erp.interfaces.dto.empresa.EmpresaCreateDto;
import com.distribuidora.erp.interfaces.dto.empresa.EmpresaResponseDto;
import com.distribuidora.erp.interfaces.dto.empresa.EmpresaUpdateDto;
import com.distribuidora.erp.interfaces.mapper.empresa.EmpresaMapper;
import com.distribuidora.erp.common.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final CatalogoUbigeoRepository catalogoUbigeoRepository;
    private final EmpresaMapper empresaMapper;

    public EmpresaService(EmpresaRepository empresaRepository,
                           CatalogoUbigeoRepository catalogoUbigeoRepository,
                           EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository;
        this.catalogoUbigeoRepository = catalogoUbigeoRepository;
        this.empresaMapper = empresaMapper;
    }

    public EmpresaResponseDto create(EmpresaCreateDto dto, String usuario) {
        Empresa empresa = new Empresa();
        empresa.setRuc(dto.getRuc());
        empresa.setRazonSocial(dto.getRazonSocial());
        empresa.setEmail(dto.getEmail());
        empresa.setTelefono(dto.getTelefono());
        empresa.setDireccion(dto.getDireccion());
        empresa.setActivo(dto.isActivo());

        if (dto.getUbigeoCodigo() != null && !dto.getUbigeoCodigo().isBlank()) {
            CatalogoUbigeo ubigeo = catalogoUbigeoRepository.findById(dto.getUbigeoCodigo())
                    .orElseThrow(() -> new NotFoundException("Ubigeo no encontrado: " + dto.getUbigeoCodigo()));
            empresa.setUbigeo(ubigeo);
        }

        OffsetDateTime now = OffsetDateTime.now();
        empresa.setFechaCreacion(now);
        empresa.setUsuarioCreacion(usuario);
        empresa.setFechaModificacion(now);
        empresa.setUsuarioModificacion(usuario);

        return empresaMapper.toResponse(empresaRepository.save(empresa));
    }

    public EmpresaResponseDto update(Long id, EmpresaUpdateDto dto, String usuario) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada id=" + id));

        if (dto.getRuc() != null) empresa.setRuc(dto.getRuc());
        if (dto.getRazonSocial() != null) empresa.setRazonSocial(dto.getRazonSocial());
        if (dto.getEmail() != null) empresa.setEmail(dto.getEmail());
        if (dto.getTelefono() != null) empresa.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) empresa.setDireccion(dto.getDireccion());
        if (dto.getActivo() != null) empresa.setActivo(dto.getActivo());

        if (dto.getUbigeoCodigo() != null) {
            if (dto.getUbigeoCodigo().isBlank()) {
                empresa.setUbigeo(null);
            } else {
                CatalogoUbigeo ubigeo = catalogoUbigeoRepository.findById(dto.getUbigeoCodigo())
                        .orElseThrow(() -> new NotFoundException("Ubigeo no encontrado: " + dto.getUbigeoCodigo()));
                empresa.setUbigeo(ubigeo);
            }
        }

        empresa.setFechaModificacion(OffsetDateTime.now());
        empresa.setUsuarioModificacion(usuario);

        return empresaMapper.toResponse(empresaRepository.save(empresa));
    }

    public EmpresaResponseDto getById(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada id=" + id));
        return empresaMapper.toResponse(empresa);
    }

    public Page<EmpresaResponseDto> list(Boolean activo, Pageable pageable) {
        Page<Empresa> page = (activo == null)
                ? empresaRepository.findAll(pageable)
                : empresaRepository.findByActivo(activo, pageable);
        return page.map(empresaMapper::toResponse);
    }

    public void delete(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new NotFoundException("Empresa no encontrada id=" + id);
        }
        empresaRepository.deleteById(id);
    }
}

