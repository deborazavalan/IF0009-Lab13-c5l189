package com.medpharm.controller;

import com.medpharm.dto.EstadoRequestDTO;
import com.medpharm.dto.RecetaRequestDTO;
import com.medpharm.dto.RecetaResponseDTO;
import com.medpharm.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recetas")
public class RecetaController {
    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @GetMapping
    public List<RecetaResponseDTO> listar() {
        return recetaService.listar();
    }

    @GetMapping("/estado/{estado}")
    public List<RecetaResponseDTO> listarPorEstado(@PathVariable String estado) {
        return recetaService.listarPorEstado(estado);
    }

    @PostMapping
    public ResponseEntity<RecetaResponseDTO> crear(@Valid @RequestBody RecetaRequestDTO request,
                                                   Authentication autenticacion) {
        RecetaResponseDTO creada = recetaService.crear(request, autenticacion.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PatchMapping("/{id}/estado")
    public RecetaResponseDTO cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequestDTO request) {
        return recetaService.cambiarEstado(id, request.estado());
    }
}
