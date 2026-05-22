package com.distribuidora.erp.application.service;

import com.distribuidora.erp.common.exception.BadRequestException;
import com.distribuidora.erp.common.exception.NotFoundException;
import com.distribuidora.erp.domain.entity.erp.Compra;
import com.distribuidora.erp.domain.entity.erp.CompraDetalle;
import com.distribuidora.erp.domain.entity.erp.Lote;
import com.distribuidora.erp.infrastructure.repository.erp.CompraRepository;
import com.distribuidora.erp.infrastructure.repository.erp.LoteRepository;
import com.distribuidora.erp.infrastructure.repository.erp.ProductoRepository;
import com.distribuidora.erp.infrastructure.repository.erp.ProveedorRepository;
import com.distribuidora.erp.interfaces.dto.compra.CompraCreateDto;
import com.distribuidora.erp.interfaces.dto.compra.CompraDetalleCreateDto;
import com.distribuidora.erp.interfaces.dto.compra.CompraResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
public class CompraService {

    private static final Set<String> TIPOS_COMPROBANTE = Set.of("FACTURA", "BOLETA", "GUIA");
    private static final Set<String> MONEDAS = Set.of("PEN", "USD");

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;

    public CompraService(
            CompraRepository compraRepository,
            ProveedorRepository proveedorRepository,
            ProductoRepository productoRepository,
            LoteRepository loteRepository) {
        this.compraRepository = compraRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    @Transactional
    public CompraResponseDto registrarCompra(CompraCreateDto dto, Long empresaId, String usuario) {
        // empresaId viene del JWT — ya validado por el filtro de seguridad
        proveedorRepository.findByEmpresaIdAndId(empresaId, dto.getProveedorId())
                .orElseThrow(() -> new NotFoundException("Proveedor no encontrado para la empresa"));

        String tipo = normalizarTipoComprobante(dto.getTipoComprobanteCodigo());
        String moneda = normalizarMoneda(dto.getMonedaCodigo());

        OffsetDateTime now = OffsetDateTime.now();

        Compra compra = new Compra();
        compra.setEmpresaId(empresaId);
        compra.setProveedorId(dto.getProveedorId());
        compra.setFechaIngreso(dto.getFechaIngreso());
        compra.setAlmacen(blankToNull(dto.getAlmacen()));
        compra.setTipoComprobanteCodigo(tipo);
        compra.setNumeroComprobante(dto.getNumeroComprobante().trim());
        compra.setMonedaCodigo(moneda);
        compra.setFechaCreacion(now);
        compra.setUsuarioCreacion(usuario);
        compra.setFechaModificacion(now);
        compra.setUsuarioModificacion(usuario);

        for (CompraDetalleCreateDto d : dto.getDetalles()) {
            if (!productoRepository.existsByEmpresaIdAndId(empresaId, d.getProductoId())) {
                throw new BadRequestException("Producto id=" + d.getProductoId() + " no pertenece a la empresa");
            }
            BigDecimal cant = d.getCantidad();
            if (cant.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Las cantidades deben ser mayores que cero");
            }
            if (!StringUtils.hasText(d.getLoteCodigo())) {
                throw new BadRequestException("Cada línea debe indicar código de lote");
            }

            CompraDetalle line = new CompraDetalle();
            line.setCompra(compra);
            line.setProductoId(d.getProductoId());
            line.setCantidad(cant);
            line.setPrecioUnitario(d.getPrecioUnitario());
            line.setLoteCodigo(d.getLoteCodigo().trim());
            line.setFechaProduccion(d.getFechaProduccion());
            line.setFechaVencimiento(d.getFechaVencimiento());
            line.setFechaCreacion(now);
            line.setUsuarioCreacion(usuario);
            line.setFechaModificacion(now);
            line.setUsuarioModificacion(usuario);
            compra.getDetalles().add(line);
        }

        Compra saved = compraRepository.save(compra);

        for (CompraDetalle line : saved.getDetalles()) {
            registrarOIncrementarStockLote(saved.getEmpresaId(), line, saved.getId(), usuario, now);
        }

        return toResponse(saved);
    }

    private void registrarOIncrementarStockLote(
            Long empresaId,
            CompraDetalle line,
            Long compraId,
            String usuario,
            OffsetDateTime now) {
        String codigo = line.getLoteCodigo().trim();
        BigDecimal entrada = Objects.requireNonNull(line.getCantidad());
        LocalDate fechaFab = line.getFechaProduccion();
        LocalDate fechaVen = line.getFechaVencimiento();
        String ref = compraRef(compraId);

        Lote lote = loteRepository
                .findByEmpresaIdAndProductoIdAndCodigoLote(empresaId, line.getProductoId(), codigo)
                .orElse(null);

        if (lote == null) {
            Lote nuevo = new Lote();
            nuevo.setEmpresaId(empresaId);
            nuevo.setProductoId(line.getProductoId());
            nuevo.setCodigoLote(codigo);
            nuevo.setFechaFabricacion(fechaFab);
            nuevo.setFechaVencimiento(fechaVen);
            nuevo.setStockActual(entrada);
            nuevo.setReferenciaDocumento(ref);
            nuevo.setActivo(true);
            nuevo.setFechaCreacion(now);
            nuevo.setUsuarioCreacion(usuario);
            nuevo.setFechaModificacion(now);
            nuevo.setUsuarioModificacion(usuario);
            loteRepository.save(nuevo);
            return;
        }

        BigDecimal actual = lote.getStockActual() != null ? lote.getStockActual() : BigDecimal.ZERO;
        lote.setStockActual(actual.add(entrada));
        if (fechaFab != null) {
            lote.setFechaFabricacion(fechaFab);
        }
        if (fechaVen != null) {
            lote.setFechaVencimiento(fechaVen);
        }
        lote.setReferenciaDocumento(ref);
        lote.setFechaModificacion(now);
        lote.setUsuarioModificacion(usuario);
        loteRepository.save(lote);
    }

    private static String compraRef(Long compraId) {
        return "CMP-" + compraId;
    }

    private String normalizarTipoComprobante(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new BadRequestException("Tipo de comprobante obligatorio");
        }
        String t = raw.trim().toUpperCase(Locale.ROOT);
        if (!TIPOS_COMPROBANTE.contains(t)) {
            throw new BadRequestException("tipoComprobanteCodigo debe ser FACTURA, BOLETA o GUIA");
        }
        return t;
    }

    private String normalizarMoneda(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new BadRequestException("Moneda obligatoria");
        }
        String m = raw.trim().toUpperCase(Locale.ROOT);
        if (!MONEDAS.contains(m)) {
            throw new BadRequestException("monedaCodigo debe ser PEN o USD");
        }
        return m;
    }

    private static String blankToNull(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static CompraResponseDto toResponse(Compra c) {
        CompraResponseDto dto = new CompraResponseDto();
        dto.setId(c.getId());
        dto.setEmpresaId(c.getEmpresaId());
        dto.setProveedorId(c.getProveedorId());
        dto.setFechaIngreso(c.getFechaIngreso());
        dto.setAlmacen(c.getAlmacen());
        dto.setTipoComprobanteCodigo(c.getTipoComprobanteCodigo());
        dto.setNumeroComprobante(c.getNumeroComprobante());
        dto.setMonedaCodigo(c.getMonedaCodigo());
        dto.setFechaCreacion(c.getFechaCreacion());
        return dto;
    }

    public List<CompraResponseDto> listarCompras(Long empresaId) {
        return compraRepository.findByEmpresaIdOrderByFechaIngresoDesc(empresaId)
                .stream()
                .map(CompraService::toResponse)
                .toList();
    }

    public CompraResponseDto obtenerCompra(Long empresaId, Long id) {
        Compra compra = compraRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new NotFoundException("Compra no encontrada: " + id));
        return toResponse(compra);
    }
}
