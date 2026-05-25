package com.distribuidora.erp.application.service;

import com.distribuidora.erp.common.exception.BadRequestException;
import com.distribuidora.erp.common.exception.NotFoundException;
import com.distribuidora.erp.domain.entity.erp.CatalogoTipoIgv;
import com.distribuidora.erp.domain.entity.erp.Producto;
import com.distribuidora.erp.domain.entity.erp.UnidadMedida;
import com.distribuidora.erp.infrastructure.repository.erp.CatalogoTipoIgvRepository;
import com.distribuidora.erp.infrastructure.repository.erp.ClaseProductoRepository;
import com.distribuidora.erp.infrastructure.repository.erp.ProductoRepository;
import com.distribuidora.erp.infrastructure.repository.erp.UnidadMedidaRepository;
import com.distribuidora.erp.interfaces.dto.producto.CatalogoOptionDto;
import com.distribuidora.erp.interfaces.dto.producto.IgvCatalogoItemDto;
import com.distribuidora.erp.interfaces.dto.producto.ProductoCreateDto;
import com.distribuidora.erp.interfaces.dto.producto.ProductoResponseDto;
import com.distribuidora.erp.interfaces.dto.producto.ProductoUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ClaseProductoRepository claseProductoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final CatalogoTipoIgvRepository catalogoTipoIgvRepository;

    public ProductoService(ProductoRepository productoRepository,
                           ClaseProductoRepository claseProductoRepository,
                           UnidadMedidaRepository unidadMedidaRepository,
                           CatalogoTipoIgvRepository catalogoTipoIgvRepository) {
        this.productoRepository = productoRepository;
        this.claseProductoRepository = claseProductoRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.catalogoTipoIgvRepository = catalogoTipoIgvRepository;
    }

    public Page<ProductoResponseDto> listarPaginado(@NonNull Long empresaId, @NonNull Pageable pageable) {
        return productoRepository.findByEmpresaId(empresaId, pageable).map(this::toResponse);
    }

    public ProductoResponseDto getById(Long id) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado id=" + id));
        return toResponse(producto);
    }

    public ProductoResponseDto crear(ProductoCreateDto dto, String usuarioAudit) {
        Long empresaId = dto.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("La empresa es obligatoria");
        }
        if (productoRepository.existsByEmpresaIdAndCodigo(empresaId, dto.getCodigo())) {
            throw new BadRequestException("El código ya existe para la empresa");
        }
        Long claseProductoId = dto.getClaseProductoId();
        if (claseProductoId == null) {
            throw new BadRequestException("La clase de producto es obligatoria");
        }
        Long unidadMedidaBaseId = dto.getUnidadMedidaBaseId();
        if (unidadMedidaBaseId == null) {
            throw new BadRequestException("La unidad de medida base es obligatoria");
        }

        claseProductoRepository.findByIdAndEmpresaId(claseProductoId, empresaId)
                .orElseThrow(() -> new NotFoundException("Clase de producto no encontrada"));
        UnidadMedida unidad = unidadMedidaRepository.findById(unidadMedidaBaseId)
                .orElseThrow(() -> new NotFoundException("Unidad de medida no encontrada"));
        if (!unidad.isActivo()) {
            throw new BadRequestException("La unidad de medida está inactiva");
        }
        Long tipoIgvId = dto.getTipoIgvId();
        if (tipoIgvId == null) {
            throw new BadRequestException("El tipo de afectación IGV es obligatorio");
        }
        CatalogoTipoIgv tipoIgv = catalogoTipoIgvRepository.findById(tipoIgvId)
                .orElseThrow(() -> new NotFoundException("Tipo IGV no encontrado"));
        if (!tipoIgv.isActivo()) {
            throw new BadRequestException("El tipo IGV está inactivo");
        }

        OffsetDateTime now = OffsetDateTime.now();
        Producto producto = new Producto();
        producto.setEmpresaId(empresaId);
        producto.setCodigo(dto.getCodigo().trim());
        producto.setNombre(dto.getNombre().trim());
        producto.setDescripcion(dto.getDescripcion());
        producto.setClaseProductoId(claseProductoId);
        producto.setUnidadMedidaBaseId(unidadMedidaBaseId);
        producto.setTipoIgvId(tipoIgvId);
        producto.setPrecioVenta(resolverPrecioVenta(dto.getPrecioVenta()));
        producto.setActivo(true);
        producto.setFechaCreacion(now);
        producto.setUsuarioCreacion(usuarioAudit);
        producto.setFechaModificacion(now);
        producto.setUsuarioModificacion(usuarioAudit);

        Producto saved = productoRepository.save(producto);
        Long savedId = saved.getId();
        if (savedId == null) {
            return toResponse(saved);
        }
        return toResponse(productoRepository.findById(savedId).orElse(saved));
    }

    public ProductoResponseDto actualizar(Long id, ProductoUpdateDto dto, String usuarioAudit) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado id=" + id));

        Long claseProductoId = dto.getClaseProductoId();
        if (claseProductoId == null) {
            throw new BadRequestException("La clase de producto es obligatoria");
        }
        Long unidadMedidaBaseId = dto.getUnidadMedidaBaseId();
        if (unidadMedidaBaseId == null) {
            throw new BadRequestException("La unidad de medida base es obligatoria");
        }

        claseProductoRepository.findByIdAndEmpresaId(claseProductoId, producto.getEmpresaId())
                .orElseThrow(() -> new NotFoundException("Clase de producto no encontrada"));
        UnidadMedida unidad = unidadMedidaRepository.findById(unidadMedidaBaseId)
                .orElseThrow(() -> new NotFoundException("Unidad de medida no encontrada"));
        if (!unidad.isActivo()) {
            throw new BadRequestException("La unidad de medida está inactiva");
        }
        Long tipoIgvId = dto.getTipoIgvId();
        if (tipoIgvId == null) {
            throw new BadRequestException("El tipo de afectación IGV es obligatorio");
        }
        CatalogoTipoIgv tipoIgv = catalogoTipoIgvRepository.findById(tipoIgvId)
                .orElseThrow(() -> new NotFoundException("Tipo IGV no encontrado"));
        if (!tipoIgv.isActivo()) {
            throw new BadRequestException("El tipo IGV está inactivo");
        }

        producto.setNombre(dto.getNombre().trim());
        producto.setDescripcion(dto.getDescripcion());
        producto.setClaseProductoId(claseProductoId);
        producto.setUnidadMedidaBaseId(unidadMedidaBaseId);
        producto.setTipoIgvId(tipoIgvId);
        if (dto.getPrecioVenta() != null) {
            producto.setPrecioVenta(resolverPrecioVenta(dto.getPrecioVenta()));
        }
        producto.setFechaModificacion(OffsetDateTime.now());
        producto.setUsuarioModificacion(usuarioAudit);

        Producto saved = productoRepository.save(producto);
        Long savedId = saved.getId();
        if (savedId == null) {
            return toResponse(saved);
        }
        return toResponse(productoRepository.findById(savedId).orElse(saved));
    }

    public void eliminarLogico(Long id, String usuarioAudit) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado id=" + id));
        producto.setActivo(false);
        producto.setFechaModificacion(OffsetDateTime.now());
        producto.setUsuarioModificacion(usuarioAudit);
        productoRepository.save(producto);
    }

    public ProductoResponseDto toggleEstado(Long id, String usuarioAudit) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado id=" + id));
        producto.setActivo(!producto.isActivo());
        producto.setFechaModificacion(OffsetDateTime.now());
        producto.setUsuarioModificacion(usuarioAudit);
        Producto saved = productoRepository.save(producto);
        Long savedId = saved.getId();
        if (savedId == null) {
            return toResponse(saved);
        }
        return toResponse(productoRepository.findById(savedId).orElse(saved));
    }

    public List<CatalogoOptionDto> listarClasesProducto(Long empresaId) {
        if (empresaId == null) {
            throw new BadRequestException("La empresa es obligatoria");
        }
        return claseProductoRepository.findByEmpresaIdAndActivoTrueOrderByNombreAsc(empresaId).stream()
                .map(c -> new CatalogoOptionDto(c.getId(), c.getNombre()))
                .toList();
    }

    public List<CatalogoOptionDto> listarUnidadesMedida() {
        return unidadMedidaRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(u -> new CatalogoOptionDto(u.getId(), u.getNombre()))
                .toList();
    }

    public List<IgvCatalogoItemDto> listarIgv() {
        return catalogoTipoIgvRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(igv -> new IgvCatalogoItemDto(igv.getId(), igv.getNombre(), igv.getPorcentaje()))
                .toList();
    }

    private ProductoResponseDto toResponse(Producto producto) {
        ProductoResponseDto dto = new ProductoResponseDto();
        dto.setId(producto.getId());
        dto.setEmpresaId(producto.getEmpresaId());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setClaseProductoId(producto.getClaseProductoId());
        dto.setClaseProductoNombre(producto.getClaseProducto() != null ? producto.getClaseProducto().getNombre() : null);
        dto.setUnidadMedidaBaseId(producto.getUnidadMedidaBaseId());
        dto.setUnidadMedidaNombre(producto.getUnidadMedidaBase() != null ? producto.getUnidadMedidaBase().getNombre() : null);
        dto.setTipoIgvId(producto.getTipoIgvId());
        if (producto.getTipoIgv() != null) {
            dto.setTipoIgvCodigo(producto.getTipoIgv().getCodigo());
            dto.setTipoIgvNombre(producto.getTipoIgv().getNombre());
            dto.setTipoIgvPorcentaje(producto.getTipoIgv().getPorcentaje());
        }
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setActivo(producto.isActivo());
        return dto;
    }

    private static BigDecimal resolverPrecioVenta(BigDecimal raw) {
        return raw != null && raw.compareTo(BigDecimal.ZERO) >= 0 ? raw : BigDecimal.ZERO;
    }
}
