package com.medpharm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecetaRequestDTO(
        @NotBlank(message = "El nombre del paciente es requerido")
        @Size(min = 5, message = "El nombre del paciente debe tener al menos 5 caracteres") String pacienteNombre,
        @NotEmpty(message = "La receta debe incluir al menos un medicamento")
        @Valid List<DetalleRequestDTO> detalles) {
}
