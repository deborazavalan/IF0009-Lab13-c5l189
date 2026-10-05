package com.medpharm.dto;

import jakarta.validation.constraints.NotBlank;

public record EstadoRequestDTO(@NotBlank(message = "El estado es requerido") String estado) {
}
