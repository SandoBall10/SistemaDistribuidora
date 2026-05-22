package com.distribuidora.erp.application.service;

import com.distribuidora.erp.common.exception.BadRequestException;
import com.distribuidora.erp.common.exception.NotFoundException;
import com.distribuidora.erp.domain.entity.erp.CatalogoTipoDocumento;
import com.distribuidora.erp.domain.entity.erp.Persona;
import com.distribuidora.erp.domain.entity.erp.Proveedor;
import com.distribuidora.erp.infrastructure.repository.erp.CatalogoTipoDocumentoRepository;
import com.distribuidora.erp.infrastructure.repository.erp.CatalogoUbigeoRepository;
import com.distribuidora.erp.infrastructure.repository.erp.PersonaRepository;
import com.distribuidora.erp.infrastructure.repository.erp.ProveedorRepository;
import com.distribuidora.erp.interfaces.dto.proveedor.ProveedorCreateDto;
import com.distribuidora.erp.interfaces.dto.proveedor.ProveedorResponseDto;
import com.distribuidora.erp.interfaces.dto.proveedor.ProveedorUpdateDto;
import com.distribuidora.erp.interfaces.dto.proveedor.TipoDocumentoCatalogoDto;
import com.distribuidora.erp.interfaces.dto.proveedor.UbigeoDistritoDto;
import com.distribuidora.erp.interfaces.dto.proveedor.UbigeoUbicacionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private static final Long TIPO_PERSONA_NATURAL = 1L;
    private static final Long TIPO_PERSONA_JURIDICA = 2L;

    /** SUNAT: DNI. */
    private static final String COD_SUNAT_DNI = "1";
    /** SUNAT: RUC. */
    private static final String COD_SUNAT_RUC = "6";

    private final ProveedorRepository proveedorRepository;
    private final PersonaRepository personaRepository;
    private final CatalogoTipoDocumentoRepository catalogoTipoDocumentoRepository;
    private final CatalogoUbigeoRepository catalogoUbigeoRepository;

    public ProveedorService(
            ProveedorRepository proveedorRepository,
            PersonaRepository personaRepository,
            CatalogoTipoDocumentoRepository catalogoTipoDocumentoRepository,
            CatalogoUbigeoRepository catalogoUbigeoRepository) {
        this.proveedorRepository = proveedorRepository;
        this.personaRepository = personaRepository;
        this.catalogoTipoDocumentoRepository = catalogoTipoDocumentoRepository;
        this.catalogoUbigeoRepository = catalogoUbigeoRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProveedorResponseDto> listarPaginado(@NonNull Long empresaId, @NonNull Pageable pageable) {
        return proveedorRepository.findByEmpresaId(empresaId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProveedorResponseDto getById(Long id) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proveedor no encontrado id=" + id));
        return toResponse(proveedor);
    }

    @Transactional
    public ProveedorResponseDto crear(ProveedorCreateDto dto, String usuarioAudit) {
        Long empresaId = dto.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("La empresa es obligatoria");
        }
        validarTipoPersonaId(dto.getTipoPersonaId());
        validarPlazoCredito(dto.getPlazoCreditoDias());
        validarUbigeoCompleto(dto.getUbigeoId());

        CatalogoTipoDocumento tipoDoc = catalogoTipoDocumentoRepository.findById(dto.getTipoDocumentoId())
                .orElseThrow(() -> new NotFoundException("Tipo de documento no encontrado"));
        if (!tipoDoc.isActivo()) {
            throw new BadRequestException("El tipo de documento está inactivo");
        }

        validarNombreSegunTipoPersona(dto.getTipoPersonaId(), dto.getNombres(), dto.getApellidoPaterno(),
                dto.getApellidoMaterno(), dto.getRazonSocial());
        validarCoherenciaTipoDocumento(tipoDoc, dto.getTipoPersonaId());

        final String numeroDocumentoNormalizado = normalizarDoc(dto.getNumeroDocumento());
        validarFormatoNumeroDocumento(tipoDoc, numeroDocumentoNormalizado);

        Persona persona = personaRepository
                .findByEmpresaIdAndTipoDocumentoIdAndNumeroDocumento(empresaId, dto.getTipoDocumentoId(), numeroDocumentoNormalizado)
                .orElse(null);

        OffsetDateTime now = OffsetDateTime.now();
        if (persona != null) {
            Long pid = persona.getId();
            if (pid != null && proveedorRepository.existsByEmpresaIdAndPersonaId(empresaId, pid)) {
                throw new BadRequestException("Ya existe un proveedor para esta combinación empresa / documento.");
            }
            aplicarDatosPersonaDesdeCreacion(persona, dto, empresaId);
            persona.setTipoPersonaId(dto.getTipoPersonaId());
            persona.setTipoDocumentoId(dto.getTipoDocumentoId());
            persona.setNumeroDocumento(numeroDocumentoNormalizado);
            persona.setFechaModificacion(now);
            persona.setUsuarioModificacion(usuarioAudit);
            persona = personaRepository.save(persona);
        } else {
            persona = nuevaPersonaDesdeCreacion(dto, empresaId, numeroDocumentoNormalizado, now, usuarioAudit);
            persona = personaRepository.save(persona);
        }

        Long personaId = persona.getId();

        String codigo = generarCodigoProveedor(empresaId, personaId);
        if (proveedorRepository.existsByEmpresaIdAndCodigoProveedor(empresaId, codigo)) {
            codigo = "P-" + empresaId + "-" + personaId;
            if (codigo.length() > 30) {
                codigo = codigo.substring(0, 30);
            }
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setEmpresaId(empresaId);
        proveedor.setPersonaId(personaId);
        proveedor.setCodigoProveedor(codigo);
        proveedor.setPlazoCreditoDias(dto.getPlazoCreditoDias());
        proveedor.setCuentaSoles(trimToNull(dto.getCuentaSoles()));
        proveedor.setActivo(true);
        proveedor.setFechaCreacion(now);
        proveedor.setUsuarioCreacion(usuarioAudit);
        proveedor.setFechaModificacion(now);
        proveedor.setUsuarioModificacion(usuarioAudit);

        Proveedor saved = proveedorRepository.save(proveedor);
        Long sid = saved.getId();
        if (sid == null) {
            return toResponse(saved);
        }
        return toResponse(proveedorRepository.findById(sid).orElse(saved));
    }

    @Transactional
    public ProveedorResponseDto actualizar(Long id, ProveedorUpdateDto dto, String usuarioAudit) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        validarTipoPersonaId(dto.getTipoPersonaId());
        validarPlazoCredito(dto.getPlazoCreditoDias());
        validarUbigeoCompleto(dto.getUbigeoId());

        CatalogoTipoDocumento tipoDoc = catalogoTipoDocumentoRepository.findById(dto.getTipoDocumentoId())
                .orElseThrow(() -> new NotFoundException("Tipo de documento no encontrado"));
        if (!tipoDoc.isActivo()) {
            throw new BadRequestException("El tipo de documento está inactivo");
        }

        validarNombreSegunTipoPersona(dto.getTipoPersonaId(), dto.getNombres(), dto.getApellidoPaterno(),
                dto.getApellidoMaterno(), dto.getRazonSocial());
        validarCoherenciaTipoDocumento(tipoDoc, dto.getTipoPersonaId());

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proveedor no encontrado id=" + id));

        Long personaPid = proveedor.getPersonaId();
        Persona persona = personaRepository.findById(personaPid)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada id=" + personaPid));

        final Long empresaId = proveedor.getEmpresaId();
        final String numeroDocNorm = normalizarDoc(dto.getNumeroDocumento());
        validarFormatoNumeroDocumento(tipoDoc, numeroDocNorm);

        personaRepository
                .findByEmpresaIdAndTipoDocumentoIdAndNumeroDocumento(empresaId, dto.getTipoDocumentoId(), numeroDocNorm)
                .filter(p -> !p.getId().equals(persona.getId()))
                .ifPresent(p -> {
                    throw new BadRequestException("Ya existe otra persona con el mismo tipo y número de documento.");
                });

        persona.setTipoPersonaId(dto.getTipoPersonaId());
        persona.setTipoDocumentoId(dto.getTipoDocumentoId());
        persona.setNumeroDocumento(numeroDocNorm);
        aplicarNombresAPersona(persona, dto.getTipoPersonaId(), dto.getNombres(), dto.getApellidoPaterno(),
                dto.getApellidoMaterno(), dto.getRazonSocial());
        persona.setDireccion(trimToNull(dto.getDireccion()));
        persona.setEmail(trimEmail(dto.getEmail()));
        persona.setTelefono(trimToNull(dto.getTelefono()));
        persona.setUbigeoCodigo(trimUbigeo(dto.getUbigeoId()));
        aplicarExtensionPeruDesdeUpdate(persona, dto);
        persona.setFechaModificacion(OffsetDateTime.now());
        persona.setUsuarioModificacion(usuarioAudit);
        personaRepository.save(persona);

        proveedor.setPlazoCreditoDias(dto.getPlazoCreditoDias());
        proveedor.setCuentaSoles(trimToNull(dto.getCuentaSoles()));
        proveedor.setFechaModificacion(OffsetDateTime.now());
        proveedor.setUsuarioModificacion(usuarioAudit);
        Proveedor updated = proveedorRepository.save(proveedor);
        Long uid = updated.getId();
        if (uid == null) {
            return toResponse(updated);
        }
        return toResponse(proveedorRepository.findById(uid).orElse(updated));
    }

    @Transactional
    public void eliminarLogico(Long id, String usuarioAudit) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proveedor no encontrado id=" + id));
        proveedor.setActivo(false);
        proveedor.setFechaModificacion(OffsetDateTime.now());
        proveedor.setUsuarioModificacion(usuarioAudit);
        proveedorRepository.save(proveedor);
    }

    @Transactional
    public ProveedorResponseDto toggleEstado(Long id, String usuarioAudit) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proveedor no encontrado id=" + id));
        proveedor.setActivo(!proveedor.isActivo());
        proveedor.setFechaModificacion(OffsetDateTime.now());
        proveedor.setUsuarioModificacion(usuarioAudit);
        Proveedor saved = proveedorRepository.save(proveedor);
        Long sid = saved.getId();
        if (sid == null) {
            return toResponse(saved);
        }
        return toResponse(proveedorRepository.findById(sid).orElse(saved));
    }

    @Transactional(readOnly = true)
    public List<TipoDocumentoCatalogoDto> listarTiposDocumento() {
        return catalogoTipoDocumentoRepository.findByActivoIsTrueOrderByNombreAsc().stream()
                .map(td -> new TipoDocumentoCatalogoDto(td.getId(), td.getNombre(), td.getCodigoSunat()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> listarDepartamentosUbigeo() {
        return catalogoUbigeoRepository.findDistinctDepartamentos();
    }

    @Transactional(readOnly = true)
    public List<String> listarProvinciasUbigeo(String departamento) {
        if (!StringUtils.hasText(departamento)) {
            throw new BadRequestException("El departamento es obligatorio para listar provincias");
        }
        return catalogoUbigeoRepository.findDistinctProvinciasByDepartamento(departamento.trim());
    }

    @Transactional(readOnly = true)
    public Optional<UbigeoUbicacionDto> buscarUbigeoPorCodigo(String codigo) {
        if (!StringUtils.hasText(codigo) || codigo.trim().length() != 6) {
            return Optional.empty();
        }
        return catalogoUbigeoRepository.findById(codigo.trim())
                .map(u -> new UbigeoUbicacionDto(
                        u.getCodigoUbigeo(),
                        u.getDepartamento(),
                        u.getProvincia(),
                        u.getDistrito()
                ));
    }

    @Transactional(readOnly = true)
    public List<UbigeoDistritoDto> listarDistritosUbigeo(String departamento, String provincia) {
        if (!StringUtils.hasText(departamento)) {
            throw new BadRequestException("El departamento es obligatorio para listar distritos");
        }
        if (!StringUtils.hasText(provincia)) {
            throw new BadRequestException("La provincia es obligatoria para listar distritos");
        }
        return catalogoUbigeoRepository
                .findByDepartamentoAndProvinciaOrderByDistrito(departamento.trim(), provincia.trim()).stream()
                .map(u -> new UbigeoDistritoDto(u.getCodigoUbigeo(), u.getDistrito()))
                .toList();
    }

    private Persona nuevaPersonaDesdeCreacion(
            ProveedorCreateDto dto,
            Long empresaId,
            String numeroDocumentoNormalizado,
            OffsetDateTime now,
            String usuarioAudit
    ) {
        Persona persona = new Persona();
        persona.setEmpresaId(empresaId);
        persona.setTipoPersonaId(dto.getTipoPersonaId());
        persona.setTipoDocumentoId(dto.getTipoDocumentoId());
        persona.setNumeroDocumento(numeroDocumentoNormalizado);
        aplicarNombresAPersona(persona, dto.getTipoPersonaId(), dto.getNombres(), dto.getApellidoPaterno(),
                dto.getApellidoMaterno(), dto.getRazonSocial());
        persona.setDireccion(trimToNull(dto.getDireccion()));
        persona.setEmail(trimEmail(dto.getEmail()));
        persona.setTelefono(trimToNull(dto.getTelefono()));
        persona.setUbigeoCodigo(trimUbigeo(dto.getUbigeoId()));
        aplicarExtensionPeruDesdeCreate(persona, dto);
        persona.setActivo(true);
        persona.setFechaCreacion(now);
        persona.setUsuarioCreacion(usuarioAudit);
        persona.setFechaModificacion(now);
        persona.setUsuarioModificacion(usuarioAudit);
        return persona;
    }

    private void aplicarDatosPersonaDesdeCreacion(Persona persona, ProveedorCreateDto dto, Long empresaId) {
        if (!empresaId.equals(persona.getEmpresaId())) {
            throw new BadRequestException("Conflicto de empresa en la persona asociada al documento.");
        }
        aplicarNombresAPersona(persona, dto.getTipoPersonaId(), dto.getNombres(), dto.getApellidoPaterno(),
                dto.getApellidoMaterno(), dto.getRazonSocial());
        persona.setDireccion(trimToNull(dto.getDireccion()));
        persona.setEmail(trimEmail(dto.getEmail()));
        persona.setTelefono(trimToNull(dto.getTelefono()));
        persona.setUbigeoCodigo(trimUbigeo(dto.getUbigeoId()));
        aplicarExtensionPeruDesdeCreate(persona, dto);
    }

    private void aplicarNombresAPersona(
            Persona persona,
            Long tipoPersonaId,
            String nombres,
            String apellidoPaterno,
            String apellidoMaterno,
            String razonSocial
    ) {
        if (TIPO_PERSONA_NATURAL.equals(tipoPersonaId)) {
            persona.setNombres(trimToNull(nombres));
            persona.setApellidoPaterno(trimToNull(apellidoPaterno));
            persona.setApellidoMaterno(trimToNull(apellidoMaterno));
            persona.setRazonSocialNombre(construirNombreCompletoNatural(nombres, apellidoPaterno, apellidoMaterno));
        } else if (TIPO_PERSONA_JURIDICA.equals(tipoPersonaId)) {
            persona.setNombres(null);
            persona.setApellidoPaterno(null);
            persona.setApellidoMaterno(null);
            String rs = trimToNull(razonSocial);
            persona.setRazonSocialNombre(rs != null ? rs : "");
        } else {
            throw new BadRequestException("Tipo de persona no soportado: " + tipoPersonaId);
        }
    }

    private String construirNombreCompletoNatural(String nombres, String apellidoPaterno, String apellidoMaterno) {
        StringBuilder sb = new StringBuilder();
        appendPartNatural(sb, nombres);
        appendPartNatural(sb, apellidoPaterno);
        appendPartNatural(sb, apellidoMaterno);
        return sb.toString().trim();
    }

    private void appendPartNatural(StringBuilder sb, String part) {
        if (!StringUtils.hasText(part)) {
            return;
        }
        if (sb.length() > 0) {
            sb.append(' ');
        }
        sb.append(part.trim());
    }

    private void validarTipoPersonaId(Long tipoPersonaId) {
        if (tipoPersonaId == null) {
            throw new BadRequestException("El tipo de persona es obligatorio");
        }
        if (!TIPO_PERSONA_NATURAL.equals(tipoPersonaId) && !TIPO_PERSONA_JURIDICA.equals(tipoPersonaId)) {
            throw new BadRequestException("Tipo de persona inválido (use Natural o Jurídica)");
        }
    }

    private void validarNombreSegunTipoPersona(Long tipoPersonaId, String nombres, String apellidoPaterno,
                                               String apellidoMaterno, String razonSocial) {
        if (TIPO_PERSONA_NATURAL.equals(tipoPersonaId)) {
            if (!StringUtils.hasText(nombres)) {
                throw new BadRequestException("Los nombres son obligatorios para persona natural");
            }
            if (!StringUtils.hasText(apellidoPaterno)) {
                throw new BadRequestException("El apellido paterno es obligatorio para persona natural");
            }
        } else {
            if (!StringUtils.hasText(razonSocial)) {
                throw new BadRequestException("La razón social es obligatoria para persona jurídica");
            }
        }
    }

    private void validarCoherenciaTipoDocumento(CatalogoTipoDocumento tipoDoc, Long tipoPersonaId) {
        if (!tipoDoc.getTipoPersonaId().equals(tipoPersonaId)) {
            throw new BadRequestException("El tipo de documento no corresponde al tipo de persona seleccionado");
        }
        String cod = tipoDoc.getCodigoSunat() != null ? tipoDoc.getCodigoSunat().trim() : "";
        if (TIPO_PERSONA_NATURAL.equals(tipoPersonaId) && !COD_SUNAT_DNI.equals(cod)) {
            throw new BadRequestException("Para persona natural el documento debe ser DNI");
        }
        if (TIPO_PERSONA_JURIDICA.equals(tipoPersonaId) && !COD_SUNAT_RUC.equals(cod)) {
            throw new BadRequestException("Para persona jurídica el documento debe ser RUC");
        }
    }

    private void validarFormatoNumeroDocumento(CatalogoTipoDocumento tipoDoc, String numeroNormalizado) {
        String cod = tipoDoc.getCodigoSunat() != null ? tipoDoc.getCodigoSunat().trim() : "";
        if (!StringUtils.hasText(numeroNormalizado)) {
            return;
        }
        if (COD_SUNAT_DNI.equals(cod)) {
            if (!numeroNormalizado.matches("^\\d{8}$")) {
                throw new BadRequestException("El DNI debe tener exactamente 8 dígitos");
            }
        } else if (COD_SUNAT_RUC.equals(cod)) {
            if (!numeroNormalizado.matches("^\\d{11}$")) {
                throw new BadRequestException("El RUC debe tener exactamente 11 dígitos");
            }
        }
    }

    private void validarUbigeoCompleto(String ubigeo) {
        validarUbigeo(ubigeo);
        if (!StringUtils.hasText(ubigeo)) {
            return;
        }
        String t = ubigeo.trim();
        catalogoUbigeoRepository.findById(t)
                .orElseThrow(() -> new BadRequestException("El ubigeo no existe en el catálogo"));
    }

    private String generarCodigoProveedor(Long empresaId, Long personaId) {
        if (personaId == null) {
            throw new BadRequestException("No se pudo generar código de proveedor");
        }
        String codigo = "P" + personaId;
        if (codigo.length() > 30) {
            codigo = codigo.substring(0, 30);
        }
        String base = codigo;
        int suffix = 1;
        while (proveedorRepository.existsByEmpresaIdAndCodigoProveedor(empresaId, codigo)) {
            String extra = base + "_" + suffix;
            codigo = extra.length() > 30 ? extra.substring(0, 30) : extra;
            suffix++;
            if (suffix > 200) {
                codigo = "P-E" + empresaId + "_" + personaId;
                codigo = codigo.length() > 30 ? codigo.substring(0, 30) : codigo;
                break;
            }
        }
        return codigo;
    }

    private ProveedorResponseDto toResponse(Proveedor p) {
        ProveedorResponseDto dto = new ProveedorResponseDto();
        dto.setId(p.getId());
        dto.setEmpresaId(p.getEmpresaId());
        dto.setPersonaId(p.getPersonaId());
        dto.setCodigoProveedor(p.getCodigoProveedor());
        dto.setPlazoCreditoDias(p.getPlazoCreditoDias());
        dto.setCuentaSoles(p.getCuentaSoles());
        dto.setActivo(p.isActivo());

        Persona persona = p.getPersona();
        if (persona == null && p.getPersonaId() != null) {
            persona = personaRepository.findById(p.getPersonaId()).orElse(null);
        }

        if (persona != null) {
            dto.setTipoPersonaId(persona.getTipoPersonaId());
            dto.setTipoDocumentoId(persona.getTipoDocumentoId());
            dto.setNumeroDocumento(persona.getNumeroDocumento());
            dto.setRazonSocialNombre(persona.getRazonSocialNombre());
            dto.setNombres(persona.getNombres());
            dto.setApellidoPaterno(persona.getApellidoPaterno());
            dto.setApellidoMaterno(persona.getApellidoMaterno());
            if (TIPO_PERSONA_JURIDICA.equals(persona.getTipoPersonaId())) {
                dto.setRazonSocial(persona.getRazonSocialNombre());
            } else {
                dto.setRazonSocial(null);
            }
            dto.setDireccion(persona.getDireccion());
            dto.setEmail(persona.getEmail());
            dto.setTelefono(persona.getTelefono());
            dto.setNombreComercial(persona.getNombreComercial());
            dto.setEstadoSunat(persona.getEstadoSunat());
            dto.setCondicionSunat(persona.getCondicionSunat());
            dto.setGenero(persona.getGenero());
            dto.setEsContribuyente(persona.isEsContribuyente());
            dto.setUbigeoId(persona.getUbigeoCodigo());
            if (persona.getTipoDocumentoId() != null) {
                catalogoTipoDocumentoRepository.findById(persona.getTipoDocumentoId())
                        .ifPresent(td -> dto.setTipoDocumentoNombre(td.getNombre()));
            }
        }

        return dto;
    }

    private void aplicarExtensionPeruDesdeCreate(Persona persona, ProveedorCreateDto dto) {
        persona.setNombreComercial(trimToNull(dto.getNombreComercial()));
        persona.setEstadoSunat(trimToNull(dto.getEstadoSunat()));
        persona.setCondicionSunat(trimToNull(dto.getCondicionSunat()));
        if (TIPO_PERSONA_JURIDICA.equals(dto.getTipoPersonaId())) {
            persona.setGenero(null);
            persona.setEsContribuyente(false);
        } else {
            persona.setGenero(trimGenero(dto.getGenero()));
            persona.setEsContribuyente(Boolean.TRUE.equals(dto.getEsContribuyente()));
        }
    }

    private void aplicarExtensionPeruDesdeUpdate(Persona persona, ProveedorUpdateDto dto) {
        persona.setNombreComercial(trimToNull(dto.getNombreComercial()));
        persona.setEstadoSunat(trimToNull(dto.getEstadoSunat()));
        persona.setCondicionSunat(trimToNull(dto.getCondicionSunat()));
        if (TIPO_PERSONA_JURIDICA.equals(dto.getTipoPersonaId())) {
            persona.setGenero(null);
            persona.setEsContribuyente(false);
        } else {
            persona.setGenero(trimGenero(dto.getGenero()));
            persona.setEsContribuyente(Boolean.TRUE.equals(dto.getEsContribuyente()));
        }
    }

    private String trimGenero(String g) {
        if (!StringUtils.hasText(g)) {
            return null;
        }
        return g.trim().substring(0, 1).toUpperCase();
    }

    private void validarPlazoCredito(Integer plazoCreditoDias) {
        if (plazoCreditoDias == null) {
            return;
        }
        if (plazoCreditoDias < 0) {
            throw new BadRequestException("El plazo de crédito debe ser mayor o igual a cero");
        }
    }

    private void validarUbigeo(String ubigeo) {
        if (!StringUtils.hasText(ubigeo)) {
            return;
        }
        String t = ubigeo.trim();
        if (t.length() != 6) {
            throw new BadRequestException("El ubigeo debe tener exactamente 6 caracteres");
        }
    }

    private String normalizarDoc(String numero) {
        if (numero == null) {
            return "";
        }
        return numero.trim().replaceAll("\\s+", "");
    }

    private String trimToNull(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        return s.trim();
    }

    private String trimEmail(String s) {
        return trimToNull(s);
    }

    private String trimUbigeo(String ubigeo) {
        if (!StringUtils.hasText(ubigeo)) {
            return null;
        }
        return ubigeo.trim();
    }
}
