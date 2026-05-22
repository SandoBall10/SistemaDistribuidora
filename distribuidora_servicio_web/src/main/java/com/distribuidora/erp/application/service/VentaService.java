package com.distribuidora.erp.application.service;

import com.distribuidora.erp.common.exception.BadRequestException;
import com.distribuidora.erp.common.exception.NotFoundException;
import com.distribuidora.erp.domain.entity.erp.Lote;
import com.distribuidora.erp.domain.entity.erp.Venta;
import com.distribuidora.erp.domain.entity.erp.VentaDetalle;
import com.distribuidora.erp.infrastructure.repository.erp.ClienteRepository;
import com.distribuidora.erp.infrastructure.repository.erp.LoteRepository;
import com.distribuidora.erp.infrastructure.repository.erp.ProductoRepository;
import com.distribuidora.erp.infrastructure.repository.erp.VentaRepository;
import com.distribuidora.erp.interfaces.dto.venta.VentaCreateDto;
import com.distribuidora.erp.interfaces.dto.venta.VentaDetalleCreateDto;
import com.distribuidora.erp.interfaces.dto.venta.VentaResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class VentaService {

    private static final BigDecimal FACTOR_IGV = new BigDecimal("1.18");
    private static final BigDecimal TOLERANCIA_TOTAL = new BigDecimal("0.02");
    private static final Set<String> TIPOS_COMPROBANTE = Set.of("FACTURA", "BOLETA");
    private static final Set<String> MONEDAS = Set.of("PEN", "USD");

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;

    public VentaService(
            VentaRepository ventaRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            LoteRepository loteRepository) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    @Transactional
    public VentaResponseDto registrarVenta(VentaCreateDto dto, Long empresaId, String usuario) {
        clienteRepository.findByEmpresaIdAndId(empresaId, dto.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado para la empresa"));

        String tipo = normalizarTipoComprobante(dto.getTipoComprobanteCodigo());
        String moneda = normalizarMoneda(dto.getMonedaCodigo());
        String serie = dto.getSerie().trim().toUpperCase(Locale.ROOT);
        String numero = dto.getNumeroComprobante().trim();

        OffsetDateTime now = OffsetDateTime.now();
        List<LineaCalculada> lineas = new ArrayList<>();

        for (VentaDetalleCreateDto d : dto.getDetalles()) {
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
            BigDecimal precio = d.getPrecioUnitario();
            if (precio.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("El precio unitario no puede ser negativo");
            }

            BigDecimal subtotal = cant.multiply(precio).setScale(2, RoundingMode.HALF_UP);
            lineas.add(new LineaCalculada(d, cant, precio, d.getLoteCodigo().trim(), subtotal));
        }

        BigDecimal totalVenta = lineas.stream()
                .map(l -> l.subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGravado = totalVenta.divide(FACTOR_IGV, 2, RoundingMode.HALF_UP);
        BigDecimal totalIgv = totalVenta.subtract(totalGravado).setScale(2, RoundingMode.HALF_UP);

        validarTotalesEnviados(dto, totalGravado, totalIgv, totalVenta);

        Venta venta = new Venta();
        venta.setEmpresaId(empresaId);
        venta.setClienteId(dto.getClienteId());
        venta.setFechaEmision(dto.getFechaEmision());
        venta.setTipoComprobanteCodigo(tipo);
        venta.setSerie(serie);
        venta.setNumeroComprobante(numero);
        venta.setMonedaCodigo(moneda);
        venta.setTotalGravado(totalGravado);
        venta.setTotalIgv(totalIgv);
        venta.setTotalVenta(totalVenta);
        venta.setFechaCreacion(now);
        venta.setUsuarioCreacion(usuario);
        venta.setFechaModificacion(now);
        venta.setUsuarioModificacion(usuario);

        for (LineaCalculada lc : lineas) {
            VentaDetalle line = new VentaDetalle();
            line.setVenta(venta);
            line.setProductoId(lc.dto.getProductoId());
            line.setCantidad(lc.cantidad);
            line.setPrecioUnitario(lc.precio);
            line.setLoteCodigo(lc.loteCodigo);
            line.setSubtotal(lc.subtotal);
            line.setFechaCreacion(now);
            line.setUsuarioCreacion(usuario);
            line.setFechaModificacion(now);
            line.setUsuarioModificacion(usuario);
            venta.getDetalles().add(line);
        }

        Venta saved = ventaRepository.save(venta);

        for (VentaDetalle line : saved.getDetalles()) {
            descontarStockLote(empresaId, line);
        }

        return toResponse(saved);
    }

    private void descontarStockLote(Long empresaId, VentaDetalle line) {
        String codigo = line.getLoteCodigo().trim();
        BigDecimal salida = line.getCantidad();

        Lote lote = loteRepository
                .findByEmpresaIdAndProductoIdAndCodigoLote(empresaId, line.getProductoId(), codigo)
                .orElseThrow(() -> new BadRequestException("Stock insuficiente para el lote " + codigo));

        BigDecimal actual = lote.getStockActual() != null ? lote.getStockActual() : BigDecimal.ZERO;
        if (actual.compareTo(salida) < 0) {
            throw new BadRequestException("Stock insuficiente para el lote " + codigo);
        }

        lote.setStockActual(actual.subtract(salida));
        lote.setReferenciaDocumento("VTA-" + line.getVenta().getId());
        lote.setFechaModificacion(OffsetDateTime.now());
        loteRepository.save(lote);
    }

    private void validarTotalesEnviados(
            VentaCreateDto dto,
            BigDecimal totalGravado,
            BigDecimal totalIgv,
            BigDecimal totalVenta) {
        if (dto.getTotalVenta() != null && !cerca(dto.getTotalVenta(), totalVenta)) {
            throw new BadRequestException("total_venta no coincide con el cálculo del servidor");
        }
        if (dto.getTotalGravado() != null && !cerca(dto.getTotalGravado(), totalGravado)) {
            throw new BadRequestException("total_gravado no coincide con el cálculo del servidor");
        }
        if (dto.getTotalIgv() != null && !cerca(dto.getTotalIgv(), totalIgv)) {
            throw new BadRequestException("total_igv no coincide con el cálculo del servidor");
        }
    }

    private static boolean cerca(BigDecimal a, BigDecimal b) {
        return a.subtract(b).abs().compareTo(TOLERANCIA_TOTAL) <= 0;
    }

    private String normalizarTipoComprobante(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new BadRequestException("Tipo de comprobante obligatorio");
        }
        String t = raw.trim().toUpperCase(Locale.ROOT);
        if (!TIPOS_COMPROBANTE.contains(t)) {
            throw new BadRequestException("tipoComprobanteCodigo debe ser FACTURA o BOLETA");
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

    private static VentaResponseDto toResponse(Venta v) {
        VentaResponseDto dto = new VentaResponseDto();
        dto.setId(v.getId());
        dto.setEmpresaId(v.getEmpresaId());
        dto.setClienteId(v.getClienteId());
        dto.setFechaEmision(v.getFechaEmision());
        dto.setTipoComprobanteCodigo(v.getTipoComprobanteCodigo());
        dto.setSerie(v.getSerie());
        dto.setNumeroComprobante(v.getNumeroComprobante());
        dto.setMonedaCodigo(v.getMonedaCodigo());
        dto.setTotalGravado(v.getTotalGravado());
        dto.setTotalIgv(v.getTotalIgv());
        dto.setTotalVenta(v.getTotalVenta());
        dto.setFechaCreacion(v.getFechaCreacion());
        return dto;
    }

    private record LineaCalculada(
            VentaDetalleCreateDto dto,
            BigDecimal cantidad,
            BigDecimal precio,
            String loteCodigo,
            BigDecimal subtotal) {
    }
}
