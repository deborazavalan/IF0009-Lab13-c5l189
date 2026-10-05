import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { Medicamento } from '../../models/medicamento.model';
import { RecetaRequest } from '../../models/receta.model';
import { MedicamentoService } from '../../services/medicamento.service';
import { RecetaService } from '../../services/receta.service';
import { positivoValidator } from '../../validators/positivo.validator';

@Component({
  selector: 'app-receta-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './receta-form.component.html',
  styleUrl: './receta-form.component.css',
})
export class RecetaFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly recetaService = inject(RecetaService);
  private readonly medicamentoService = inject(MedicamentoService);

  protected readonly medicamentos = signal<Medicamento[]>([]);
  protected readonly enviando = signal(false);
  protected readonly errorMensaje = signal<string | null>(null);

  /** Formulario anidado: FormGroup con un FormArray de renglones de medicamento. */
  protected readonly form = this.fb.nonNullable.group({
    pacienteNombre: ['', [Validators.required, Validators.minLength(5)]],
    detalles: this.fb.array([this.crearDetalle()]),
  });

  protected get detalles() {
    return this.form.controls.detalles;
  }

  ngOnInit(): void {
    this.medicamentoService.listar().subscribe({
      next: (lista) => this.medicamentos.set(lista),
      error: (error: HttpErrorResponse) =>
        this.errorMensaje.set(error.error?.detail ?? 'No se pudo cargar el catálogo de medicamentos.'),
    });
  }

  private crearDetalle() {
    return this.fb.group({
      medicamentoId: this.fb.control<number | null>(null, [Validators.required]),
      cantidad: this.fb.control<number | null>(1, [Validators.required, positivoValidator]),
      dosisIndicada: this.fb.nonNullable.control(''),
    });
  }

  protected agregarRenglon(): void {
    this.detalles.push(this.crearDetalle());
  }

  protected quitarRenglon(indice: number): void {
    if (this.detalles.length > 1) {
      this.detalles.removeAt(indice);
    }
  }

  protected stockDe(medicamentoId: number | null): number | null {
    if (medicamentoId === null) {
      return null;
    }
    return this.medicamentos().find((m) => m.id === medicamentoId)?.stock ?? null;
  }

  protected pacienteInvalido(): boolean {
    const control = this.form.controls.pacienteNombre;
    return control.invalid && (control.touched || control.dirty);
  }

  protected enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const valor = this.form.getRawValue();
    const payload: RecetaRequest = {
      pacienteNombre: valor.pacienteNombre.trim(),
      detalles: valor.detalles.map((d) => ({
        medicamentoId: Number(d.medicamentoId),
        cantidad: Number(d.cantidad),
        dosisIndicada: d.dosisIndicada.trim(),
      })),
    };

    this.enviando.set(true);
    this.errorMensaje.set(null);

    this.recetaService.crear(payload).subscribe({
      next: () => {
        this.enviando.set(false);
        this.router.navigate(['/recetas']);
      },
      error: (error: HttpErrorResponse) => {
        this.enviando.set(false);
        this.errorMensaje.set(
          error.status === 0
            ? 'No se pudo conectar con el servidor. ¿Está corriendo el backend en el puerto 8080?'
            : (error.error?.detail ?? 'No se pudo registrar la receta.'),
        );
      },
    });
  }
}
