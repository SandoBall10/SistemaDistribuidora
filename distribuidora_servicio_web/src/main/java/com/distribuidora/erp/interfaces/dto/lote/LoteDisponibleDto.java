package com.distribuidora.erp.interfaces.dto.lote;

import java.math.BigDecimal;

public class LoteDisponibleDto {

    private String codigoLote;
    private BigDecimal stockActual;

    public LoteDisponibleDto() {
    }

    public LoteDisponibleDto(String codigoLote, BigDecimal stockActual) {
        this.codigoLote = codigoLote;
        this.stockActual = stockActual;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public void setStockActual(BigDecimal stockActual) {
        this.stockActual = stockActual;
    }
}
