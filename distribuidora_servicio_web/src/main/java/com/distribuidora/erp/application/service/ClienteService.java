package com.distribuidora.erp.application.service;

import com.distribuidora.erp.common.exception.BadRequestException;
import com.distribuidora.erp.common.exception.NotFoundException;
import com.distribuidora.erp.domain.entity.erp.CatalogoTipoDocumento;
import com.distribuidora.erp.domain.entity.erp.Cliente;
import com.distribuidora.erp.domain.entity.erp.Persona;
import com.distribuidora.erp.infrastructure.repository.erp.CatalogoTipoDocumentoRepository;
import com.distribuidora.erp.infrastructure.repository.erp.ClienteRepository;
import com.distribuidora.erp.infrastructure.repository.erp.PersonaRepository;
import com.distribuidora.erp.interfaces.dto.cliente.ClienteCreateDto;
import com.distribuidora.erp.interfaces.dto.cliente.ClienteResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class ClienteService {

    private static final Long TIPO_PERSONA_NATURAL = 1L;
    private static final Long TIPO_PERSONA_JURIDICA = 2L;
    private static final String COD_SUNAT_DNI = "1";
    private static final String COD_SUNAT_RUC = "6";

    private final ClienteRepository clienteRepository;
    private final PersonaRepository personaRepository;
    private final CatalogoTipoDocumentoRepository catalogoTipoDocumentoRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            PersonaRepository personaRepository,
            CatalogoTipoDocumentoRepository catalogoTipoDocumentoRepository) {
        this.clienteRepository = clienteRepository;
        this.personaRepository = personaRepository;
        this.catalogoTipoDocumentoRepository = catalogoTipoDocumentoRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDto> listarActivos(Long empresaId) {
        return clienteRepository.findByEmpresaIdAndActivoTrueOrderByCodigoClienteAsc(empresaId)
                .stream()
                .map(ClienteService::toResponse)
                .toList();
    }

    @Transactional
    public ClienteResponseDto crear(ClienteCreateDto dto, Long empresaId, String usuarioAudit) {
        String tipoDoc = dto.getTipoDocumento().trim().toUpperCase(Locale.ROOT);
        Long tipoPersonaId = "DNI".equals(tipoDoc) ? TIPO_PERSONA_NATURAL : TIPO_PERSONA_JURIDICA;
        String codSunat = "DNI".equals(tipoDoc) ? COD_SUNAT_DNI : COD_SUNAT_RUC;

        CatalogoTipoDocumento tipoDocumento = catalogoTipoDocumentoRepository.findAll().stream()
                .filter(t -> t.isActivo() && codSunat.equals(trim(t.getCodigoSunat())))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Tipo de documento " + tipoDoc + " no configurado en catálogo"));

        if (!tipoDocumento.getTipoPersonaId().equals(tipoPersonaId)) {
            throw new BadRequestException("Incoherencia entre tipo de documento y persona");
        }

        final String numeroDocumento = normalizarDoc(dto.getNumeroDocumento());
        validarFormatoNumero(tipoDoc, numeroDocumento);

        if (!StringUtils.hasText(dto.getRazonSocialNombre())) {
            throw new BadRequestException("El nombre o razón social es obligatorio");
        }

        Persona persona = personaRepository
                .findByEmpresaIdAndTipoDocumentoIdAndNumeroDocumento(empresaId, tipoDocumento.getId(), numeroDocumento)
                .orElse(null);

        OffsetDateTime now = OffsetDateTime.now();
        if (persona != null) {
            Long pid = persona.getId();
            if (pid != null && clienteRepository.existsByEmpresaIdAndPersonaId(empresaId, pid)) {
                throw new BadRequestException("Ya existe un cliente registrado con este documento.");
            }
            aplicarDatosPersona(persona, dto, tipoPersonaId, tipoDocumento.getId(), numeroDocumento, now, usuarioAudit);
            persona = personaRepository.save(persona);
        } else {
            persona = nuevaPersona(dto, empresaId, tipoPersonaId, tipoDocumento.getId(), numeroDocumento, now, usuarioAudit);
            persona = personaRepository.save(persona);
        }

        Long personaId = persona.getId();
        String codigo = generarCodigoCliente(empresaId, personaId);

        Cliente cliente = new Cliente();
        cliente.setEmpresaId(empresaId);
        cliente.setPersonaId(personaId);
        cliente.setCodigoCliente(codigo);
        cliente.setActivo(true);
        cliente.setFechaCreacion(now);
        cliente.setUsuarioCreacion(usuarioAudit);
        cliente.setFechaModificacion(now);
        cliente.setUsuarioModificacion(usuarioAudit);

        Cliente saved = clienteRepository.save(cliente);
        return clienteRepository.findByEmpresaIdAndId(empresaId, saved.getId())
                .map(ClienteService::toResponse)
                .orElse(toResponse(saved));
    }

    private Persona nuevaPersona(
            ClienteCreateDto dto,
            Long empresaId,
            Long tipoPersonaId,
            Long tipoDocumentoId,
            String numeroDocumento,
            OffsetDateTime now,
            String usuarioAudit) {
        Persona persona = new Persona();
        persona.setEmpresaId(empresaId);
        persona.setTipoPersonaId(tipoPersonaId);
        persona.setTipoDocumentoId(tipoDocumentoId);
        persona.setNumeroDocumento(numeroDocumento);
        aplicarDatosPersona(persona, dto, tipoPersonaId, tipoDocumentoId, numeroDocumento, now, usuarioAudit);
        persona.setActivo(true);
        persona.setFechaCreacion(now);
        persona.setUsuarioCreacion(usuarioAudit);
        persona.setFechaModificacion(now);
        persona.setUsuarioModificacion(usuarioAudit);
        return persona;
    }

    private void aplicarDatosPersona(
            Persona persona,
            ClienteCreateDto dto,
            Long tipoPersonaId,
            Long tipoDocumentoId,
            String numeroDocumento,
            OffsetDateTime now,
            String usuarioAudit) {
        persona.setTipoPersonaId(tipoPersonaId);
        persona.setTipoDocumentoId(tipoDocumentoId);
        persona.setNumeroDocumento(numeroDocumento);

        if (TIPO_PERSONA_NATURAL.equals(tipoPersonaId)) {
            String nombres = trimToNull(dto.getNombres());
            String apPat = trimToNull(dto.getApellidoPaterno());
            String apMat = trimToNull(dto.getApellidoMaterno());
            if (nombres != null) {
                persona.setNombres(nombres);
                persona.setApellidoPaterno(apPat);
                persona.setApellidoMaterno(apMat);
                persona.setRazonSocialNombre(construirNombreNatural(nombres, apPat, apMat));
            } else {
                persona.setRazonSocialNombre(dto.getRazonSocialNombre().trim());
            }
        } else {
            persona.setNombres(null);
            persona.setApellidoPaterno(null);
            persona.setApellidoMaterno(null);
            persona.setRazonSocialNombre(dto.getRazonSocialNombre().trim());
        }

        persona.setDireccion(trimToNull(dto.getDireccion()));
        persona.setEstadoSunat(trimToNull(dto.getEstadoSunat()));
        persona.setCondicionSunat(trimToNull(dto.getCondicionSunat()));
        persona.setEsContribuyente(TIPO_PERSONA_JURIDICA.equals(tipoPersonaId));
        persona.setFechaModificacion(now);
        persona.setUsuarioModificacion(usuarioAudit);
    }

    private static String construirNombreNatural(String nombres, String apPat, String apMat) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(nombres)) {
            sb.append(nombres.trim());
        }
        if (StringUtils.hasText(apPat)) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(apPat.trim());
        }
        if (StringUtils.hasText(apMat)) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(apMat.trim());
        }
        return sb.toString().trim();
    }

    private void validarFormatoNumero(String tipoDoc, String numero) {
        if ("DNI".equals(tipoDoc) && !numero.matches("^\\d{8}$")) {
            throw new BadRequestException("El DNI debe tener exactamente 8 dígitos");
        }
        if ("RUC".equals(tipoDoc) && !numero.matches("^\\d{11}$")) {
            throw new BadRequestException("El RUC debe tener exactamente 11 dígitos");
        }
    }

    private String generarCodigoCliente(Long empresaId, Long personaId) {
        String codigo = "C" + personaId;
        if (codigo.length() > 30) {
            codigo = codigo.substring(0, 30);
        }
        String base = codigo;
        int suffix = 1;
        while (clienteRepository.existsByEmpresaIdAndCodigoCliente(empresaId, codigo)) {
            String extra = base + "_" + suffix;
            codigo = extra.length() > 30 ? extra.substring(0, 30) : extra;
            suffix++;
        }
        return codigo;
    }

    private static String normalizarDoc(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new BadRequestException("El número de documento es obligatorio");
        }
        return raw.replaceAll("\\D", "");
    }

    private static String trim(String s) {
        return s != null ? s.trim() : "";
    }

    private static String trimToNull(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static ClienteResponseDto toResponse(Cliente c) {
        ClienteResponseDto dto = new ClienteResponseDto();
        dto.setId(c.getId());
        dto.setEmpresaId(c.getEmpresaId());
        dto.setCodigoCliente(c.getCodigoCliente());
        dto.setActivo(c.isActivo());

        Persona p = c.getPersona();
        if (p != null) {
            dto.setNumeroDocumento(p.getNumeroDocumento());
            dto.setRazonSocialNombre(p.getRazonSocialNombre());
            dto.setDireccion(p.getDireccion());
            dto.setTipoDocumentoId(p.getTipoDocumentoId());
        }
        return dto;
    }
}
