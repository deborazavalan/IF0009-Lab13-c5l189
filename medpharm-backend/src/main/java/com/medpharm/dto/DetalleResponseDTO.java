package com.medpharm.dto;

import com.medpharm.model.DetalleReceta;

public record DetalleResponseDTO(Long id, Long medicamentoId, String medicamentoNombre,
                                 Integer cantidad, String dosisIndicada) {
    public static DetalleResponseDTO desde(DetalleReceta d) {
        return new DetalleResponseDTO(
                d.getId(),
                d.getMedicamento().getId(),
                d.getMedicamento().getNombre(),
                d.getCantidad(),
                d.getDosisIndicada());
    }
}
