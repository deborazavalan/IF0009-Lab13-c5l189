package com.medpharm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetalleRequestDTO(
        @NotNull(message = "El medicamento es requerido") Long medicamentoId,
        @NotNull(message = "La cantidad es requerida")
        @Positive(message = "La cantidad debe ser mayor a 0") Integer cantidad,
        String dosisIndicada) {
}
