package com.medpharm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalles_receta")
public class DetalleReceta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receta_id", nullable = false)
    private RecetaMedica receta;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false)
    private Integer cantidad;

    private String dosisIndicada;

    public DetalleReceta() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RecetaMedica getReceta() { return receta; }
    public void setReceta(RecetaMedica receta) { this.receta = receta; }

    public Medicamento getMedicamento() { return medicamento; }
    public void setMedicamento(Medicamento medicamento) { this.medicamento = medicamento; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getDosisIndicada() { return dosisIndicada; }
    public void setDosisIndicada(String dosisIndicada) { this.dosisIndicada = dosisIndicada; }
}
