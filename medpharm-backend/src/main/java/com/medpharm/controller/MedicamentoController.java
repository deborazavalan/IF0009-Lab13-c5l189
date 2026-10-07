package com.medpharm.controller;

import com.medpharm.dto.MedicamentoDTO;
import com.medpharm.repository.MedicamentoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medicamentos")
public class MedicamentoController {
    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoController(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    @GetMapping
    public List<MedicamentoDTO> listar() {
        return medicamentoRepository.findAllByOrderByNombreAsc().stream()
                .map(MedicamentoDTO::desde)
                .toList();
    }
}
