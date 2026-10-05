package com.medpharm.repository;

import com.medpharm.model.RecetaMedica;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecetaRepository extends JpaRepository<RecetaMedica, Long> {

    @EntityGraph(attributePaths = {"medico", "detalles", "detalles.medicamento"})
    List<RecetaMedica> findAllByOrderByFechaEmisionDesc();

    @EntityGraph(attributePaths = {"medico", "detalles", "detalles.medicamento"})
    List<RecetaMedica> findByEstadoOrderByFechaEmisionDesc(String estado);

    @EntityGraph(attributePaths = {"medico", "detalles", "detalles.medicamento"})
    Optional<RecetaMedica> findWithDetallesById(Long id);
}
