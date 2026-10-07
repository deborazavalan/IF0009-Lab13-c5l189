package com.medpharm.service;

import com.medpharm.dto.DetalleRequestDTO;
import com.medpharm.dto.RecetaRequestDTO;
import com.medpharm.dto.RecetaResponseDTO;
import com.medpharm.exception.RecetaNoModificableException;
import com.medpharm.exception.RecursoNoEncontradoException;
import com.medpharm.exception.SolicitudInvalidaException;
import com.medpharm.exception.StockInsuficienteException;
import com.medpharm.model.DetalleReceta;
import com.medpharm.model.Medicamento;
import com.medpharm.model.RecetaMedica;
import com.medpharm.model.Usuario;
import com.medpharm.repository.MedicamentoRepository;
import com.medpharm.repository.RecetaRepository;
import com.medpharm.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class RecetaService {
    public static final String PENDIENTE = "PENDIENTE";
    public static final String DESPACHADA = "DESPACHADA";
    public static final String CANCELADA = "CANCELADA";

    private static final Set<String> ESTADOS_VALIDOS = Set.of(PENDIENTE, DESPACHADA, CANCELADA);

    private final RecetaRepository recetaRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public RecetaService(RecetaRepository recetaRepository,
                         MedicamentoRepository medicamentoRepository,
                         UsuarioRepository usuarioRepository) {
        this.recetaRepository = recetaRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> listar() {
        return recetaRepository.findAllByOrderByFechaEmisionDesc().stream()
                .map(RecetaResponseDTO::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> listarPorEstado(String estado) {
        String normalizado = normalizarEstado(estado);
        return recetaRepository.findByEstadoOrderByFechaEmisionDesc(normalizado).stream()
                .map(RecetaResponseDTO::desde)
                .toList();
    }

    @Transactional
    public RecetaResponseDTO crear(RecetaRequestDTO dto, String usernameMedico) {
        Usuario medico = usuarioRepository.findByUsername(usernameMedico)
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico no encontrado: " + usernameMedico));

        Map<Long, Integer> cantidadPorMedicamento = new LinkedHashMap<>();
        for (DetalleRequestDTO d : dto.detalles()) {
            cantidadPorMedicamento.merge(d.medicamentoId(), d.cantidad(), Integer::sum);
        }

        Map<Long, Medicamento> medicamentos = new LinkedHashMap<>();
        for (Map.Entry<Long, Integer> e : cantidadPorMedicamento.entrySet()) {
            Medicamento m = medicamentoRepository.findById(e.getKey())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Medicamento con id " + e.getKey() + " no existe"));
            if (e.getValue() > m.getStock()) {
                throw new StockInsuficienteException("Stock insuficiente de " + m.getNombre()
                        + ": se solicitan " + e.getValue() + " y hay " + m.getStock() + " disponibles");
            }
            medicamentos.put(m.getId(), m);
        }

        RecetaMedica receta = new RecetaMedica();
        receta.setCodigoReceta("RX-" + LocalDateTime.now().getYear() + "-"
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        receta.setPacienteNombre(dto.pacienteNombre().trim());
        receta.setMedico(medico);
        receta.setEstado(PENDIENTE);
        receta.setFechaEmision(LocalDateTime.now());

        for (DetalleRequestDTO d : dto.detalles()) {
            DetalleReceta detalle = new DetalleReceta();
            detalle.setMedicamento(medicamentos.get(d.medicamentoId()));
            detalle.setCantidad(d.cantidad());
            detalle.setDosisIndicada(d.dosisIndicada());
            receta.agregarDetalle(detalle);
        }

        return RecetaResponseDTO.desde(recetaRepository.save(receta));
    }

    @Transactional
    public RecetaResponseDTO cambiarEstado(Long id, String nuevoEstado) {
        String estado = normalizarEstado(nuevoEstado);
        if (!DESPACHADA.equals(estado) && !CANCELADA.equals(estado)) {
            throw new SolicitudInvalidaException("Solo se permite cambiar el estado a DESPACHADA o CANCELADA");
        }

        RecetaMedica receta = recetaRepository.findWithDetallesById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta con id " + id + " no existe"));

        if (!PENDIENTE.equals(receta.getEstado())) {
            throw new RecetaNoModificableException(
                    "La receta " + receta.getCodigoReceta() + " ya está " + receta.getEstado()
                            + " y no puede modificarse");
        }

        if (DESPACHADA.equals(estado)) {
            for (DetalleReceta d : receta.getDetalles()) {
                Medicamento m = d.getMedicamento();
                if (d.getCantidad() > m.getStock()) {
                    throw new StockInsuficienteException("No se puede despachar: stock insuficiente de "
                            + m.getNombre() + " (" + m.getStock() + " disponibles, " + d.getCantidad() + " requeridos)");
                }
                m.setStock(m.getStock() - d.getCantidad());
                medicamentoRepository.save(m);
            }
        }

        receta.setEstado(estado);
        return RecetaResponseDTO.desde(recetaRepository.save(receta));
    }

    private String normalizarEstado(String estado) {
        String normalizado = estado == null ? "" : estado.trim().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(normalizado)) {
            throw new SolicitudInvalidaException(
                    "Estado inválido: '" + estado + "'. Valores permitidos: PENDIENTE, DESPACHADA, CANCELADA");
        }
        return normalizado;
    }
}
