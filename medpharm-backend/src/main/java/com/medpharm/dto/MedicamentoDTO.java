package com.medpharm.dto;

import com.medpharm.model.Medicamento;

import java.math.BigDecimal;

public record MedicamentoDTO(Long id, String codigo, String nombre, Integer stock, BigDecimal precioUnitario) {
    public static MedicamentoDTO desde(Medicamento m) {
        return new MedicamentoDTO(m.getId(), m.getCodigo(), m.getNombre(), m.getStock(), m.getPrecioUnitario());
    }
}
