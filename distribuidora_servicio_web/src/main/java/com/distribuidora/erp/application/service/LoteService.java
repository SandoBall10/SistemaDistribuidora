package com.distribuidora.erp.application.service;

import com.distribuidora.erp.common.exception.BadRequestException;
import com.distribuidora.erp.common.exception.NotFoundException;
import com.distribuidora.erp.domain.entity.erp.Lote;
import com.distribuidora.erp.infrastructure.repository.erp.LoteRepository;
import com.distribuidora.erp.infrastructure.repository.erp.ProductoRepository;
import com.distribuidora.erp.interfaces.dto.lote.LoteDisponibleDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;

    public LoteService(LoteRepository loteRepository, ProductoRepository productoRepository) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
    }

    public List<LoteDisponibleDto> listarDisponibles(Long empresaId, Long productoId) {
        if (productoId == null) {
            throw new BadRequestException("productoId es obligatorio");
        }
        if (!productoRepository.existsByEmpresaIdAndId(empresaId, productoId)) {
            throw new NotFoundException("Producto no encontrado para la empresa");
        }
        return loteRepository.findDisponiblesByEmpresaIdAndProductoId(empresaId, productoId).stream()
                .map(LoteService::toDto)
                .toList();
    }

    private static LoteDisponibleDto toDto(Lote lote) {
        return new LoteDisponibleDto(lote.getCodigoLote(), lote.getStockActual());
    }
}
