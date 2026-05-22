package com.distribuidora.erp.application.service;

import com.distribuidora.erp.common.exception.BadRequestException;
import com.distribuidora.erp.common.exception.NotFoundException;
import com.distribuidora.erp.domain.entity.erp.Persona;
import com.distribuidora.erp.domain.entity.erp.Rol;
import com.distribuidora.erp.domain.entity.erp.Usuario;
import com.distribuidora.erp.infrastructure.repository.erp.PersonaRepository;
import com.distribuidora.erp.infrastructure.repository.erp.RolRepository;
import com.distribuidora.erp.infrastructure.repository.erp.UsuarioRepository;
import com.distribuidora.erp.interfaces.dto.usuario.UsuarioCreateDto;
import com.distribuidora.erp.interfaces.dto.usuario.UsuarioResponseDto;
import com.distribuidora.erp.interfaces.dto.usuario.UsuarioUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PersonaRepository personaRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UsuarioResponseDto> list(@NonNull Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::toResponse);
    }

    public UsuarioResponseDto getById(Long id) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado id=" + id));
        return toResponse(usuario);
    }

    public UsuarioResponseDto create(UsuarioCreateDto dto, String usuarioAudit) {
        Long empresaId = dto.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("La empresa es obligatoria");
        }

        if (usuarioRepository.findByEmpresaIdAndNombreUsuario(empresaId, dto.getNombreUsuario()).isPresent()) {
            throw new BadRequestException("El nombre de usuario ya existe para la empresa");
        }

        Rol rol = rolRepository.findByCodigo(dto.getRolCodigo())
                .orElseThrow(() -> new NotFoundException("Rol no encontrado: " + dto.getRolCodigo()));

        Persona persona = personaRepository
                .findByEmpresaIdAndNumeroDocumento(
                        empresaId,
                        dto.getNumeroDocumento()
                )
                .orElseGet(() -> createPersona(dto));

        OffsetDateTime now = OffsetDateTime.now();

        Usuario usuario = new Usuario();
        usuario.setEmpresaId(empresaId);
        usuario.setPersonaId(persona.getId());
        usuario.setRolId(rol.getId());
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setActivo(true);
        usuario.setFechaCreacion(now);
        usuario.setFechaModificacion(now);
        usuario.setUsuarioCreacion(usuarioAudit);
        usuario.setUsuarioModificacion(usuarioAudit);

        Usuario saved = usuarioRepository.save(usuario);
        Long savedId = saved.getId();
        if (savedId == null) {
            return toResponse(saved);
        }
        return toResponse(usuarioRepository.findById(savedId).orElse(saved));
    }

    public UsuarioResponseDto update(Long id, UsuarioUpdateDto dto, String usuarioAudit) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado id=" + id));

        Rol rol = rolRepository.findByCodigo(dto.getRolCodigo())
                .orElseThrow(() -> new NotFoundException("Rol no encontrado: " + dto.getRolCodigo()));

        Long personaId = usuario.getPersonaId();
        if (personaId == null) {
            throw new NotFoundException("Persona no encontrada para usuario id=" + id);
        }
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada id=" + usuario.getPersonaId()));

        persona.setNumeroDocumento(dto.getNumeroDocumento());
        persona.setRazonSocialNombre((dto.getNombres() + " " + dto.getApellidos()).trim());
        persona.setFechaModificacion(OffsetDateTime.now());
        persona.setUsuarioModificacion(usuarioAudit);
        personaRepository.save(persona);

        usuario.setRolId(rol.getId());
        usuario.setFechaModificacion(OffsetDateTime.now());
        usuario.setUsuarioModificacion(usuarioAudit);
        Usuario updated = usuarioRepository.save(usuario);
        Long updatedId = updated.getId();
        if (updatedId == null) {
            return toResponse(updated);
        }
        return toResponse(usuarioRepository.findById(updatedId).orElse(updated));
    }

    public void softDelete(Long id, String usuarioAudit) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado id=" + id));

        usuario.setActivo(false);
        usuario.setFechaModificacion(OffsetDateTime.now());
        usuario.setUsuarioModificacion(usuarioAudit);
        usuarioRepository.save(usuario);
    }

    public UsuarioResponseDto toggleEstado(Long id, String usuarioAudit) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado id=" + id));

        usuario.setActivo(!usuario.isActivo());
        usuario.setFechaModificacion(OffsetDateTime.now());
        usuario.setUsuarioModificacion(usuarioAudit);
        Usuario saved = usuarioRepository.save(usuario);
        Long savedId = saved.getId();
        if (savedId == null) {
            return toResponse(saved);
        }
        return toResponse(usuarioRepository.findById(savedId).orElse(saved));
    }

    private Persona createPersona(UsuarioCreateDto dto) {
        Long empresaId = dto.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("La empresa es obligatoria");
        }
        OffsetDateTime now = OffsetDateTime.now();
        Persona persona = new Persona();
        persona.setEmpresaId(empresaId);
        persona.setNumeroDocumento(dto.getNumeroDocumento());
        persona.setTipoPersonaId(1L);
        persona.setTipoDocumentoId(1L);
        persona.setRazonSocialNombre((dto.getNombres() + " " + dto.getApellidos()).trim());
        persona.setActivo(true);
        persona.setFechaCreacion(now);
        persona.setFechaModificacion(now);
        persona.setUsuarioCreacion("system");
        persona.setUsuarioModificacion("system");
        return personaRepository.save(persona);
    }

    private UsuarioResponseDto toResponse(Usuario usuario) {
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(usuario.getId());
        dto.setEmpresaId(usuario.getEmpresaId());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setRolCodigo(usuario.getRol() != null ? usuario.getRol().getCodigo() : null);
        dto.setActivo(usuario.isActivo());

        if (usuario.getPersona() != null) {
            dto.setPersonaNombreCompleto(usuario.getPersona().getRazonSocialNombre());
            dto.setTipoDocumento(
                    usuario.getPersona().getTipoDocumentoId() != null
                            ? String.valueOf(usuario.getPersona().getTipoDocumentoId())
                            : null
            );
            dto.setNumeroDocumento(usuario.getPersona().getNumeroDocumento());
        }

        return dto;
    }
}
