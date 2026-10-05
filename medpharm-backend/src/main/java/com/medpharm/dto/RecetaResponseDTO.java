package com.medpharm.dto;

import com.medpharm.model.RecetaMedica;

import java.time.LocalDateTime;
import java.util.List;

public record RecetaResponseDTO(Long id, String codigoReceta, String pacienteNombre, String medicoNombre,
                                String estado, LocalDateTime fechaEmision, List<DetalleResponseDTO> detalles) {

    public static RecetaResponseDTO desde(RecetaMedica r) {
        return new RecetaResponseDTO(
                r.getId(),
                r.getCodigoReceta(),
                r.getPacienteNombre(),
                r.getMedico().getNombreCompleto(),
                r.getEstado(),
                r.getFechaEmision(),
                r.getDetalles().stream().map(DetalleResponseDTO::desde).toList());
    }
}
