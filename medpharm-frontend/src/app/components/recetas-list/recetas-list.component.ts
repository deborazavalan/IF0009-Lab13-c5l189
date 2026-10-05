import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { EstadoReceta, Receta } from '../../models/receta.model';
import { AuthService } from '../../services/auth.service';
import { RecetaService } from '../../services/receta.service';

export type FiltroEstado = 'TODAS' | EstadoReceta;

@Component({
  selector: 'app-recetas-list',
  imports: [DatePipe, RouterLink],
  templateUrl: './recetas-list.component.html',
  styleUrl: './recetas-list.component.css',
})
export class RecetasListComponent implements OnInit {
  private readonly recetaService = inject(RecetaService);
  protected readonly auth = inject(AuthService);

  protected readonly filtros: FiltroEstado[] = ['TODAS', 'PENDIENTE', 'DESPACHADA', 'CANCELADA'];

  /** Estado reactivo con Angular Signals */
  protected readonly recetas = signal<Receta[]>([]);
  protected readonly filtro = signal<FiltroEstado>('TODAS');
  protected readonly cargando = signal(true);
  protected readonly errorMensaje = signal<string | null>(null);
  protected readonly procesandoId = signal<number | null>(null);

  /** Lista derivada: se recalcula sola cuando cambian las recetas o el filtro */
  protected readonly recetasFiltradas = computed(() => {
    const estado = this.filtro();
    return estado === 'TODAS' ? this.recetas() : this.recetas().filter((r) => r.estado === estado);
  });

  protected readonly pendientes = computed(() => this.recetas().filter((r) => r.estado === 'PENDIENTE').length);
  protected readonly despachadas = computed(() => this.recetas().filter((r) => r.estado === 'DESPACHADA').length);

  protected readonly puedeCambiarEstado = computed(() => this.auth.rol() === 'FARMACEUTICO');

  ngOnInit(): void {
    this.cargar();
  }

  protected cargar(): void {
    this.cargando.set(true);
    this.errorMensaje.set(null);

    this.recetaService.listar().subscribe({
      next: (recetas) => {
        this.recetas.set(recetas);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.errorMensaje.set(this.mensajeDeError(error, 'No se pudo cargar el listado de recetas.'));
      },
    });
  }

  protected seleccionarFiltro(filtro: FiltroEstado): void {
    this.filtro.set(filtro);
  }

  protected contar(filtro: FiltroEstado): number {
    return filtro === 'TODAS' ? this.recetas().length : this.recetas().filter((r) => r.estado === filtro).length;
  }

  /** PATCH /recetas/{id}/estado y actualiza solo la fila afectada en el signal. */
  protected cambiarEstado(receta: Receta, estado: EstadoReceta): void {
    this.procesandoId.set(receta.id);
    this.errorMensaje.set(null);

    this.recetaService.cambiarEstado(receta.id, estado).subscribe({
      next: (actualizada) => {
        this.recetas.update((lista) => lista.map((r) => (r.id === actualizada.id ? actualizada : r)));
        this.procesandoId.set(null);
      },
      error: (error: HttpErrorResponse) => {
        this.procesandoId.set(null);
        this.errorMensaje.set(this.mensajeDeError(error, 'No se pudo actualizar el estado de la receta.'));
      },
    });
  }

  protected claseBadge(estado: EstadoReceta): string {
    return `badge badge-${estado.toLowerCase()}`;
  }

  private mensajeDeError(error: HttpErrorResponse, porDefecto: string): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. ¿Está corriendo el backend en el puerto 8080?';
    }
    return error.error?.detail ?? porDefecto;
  }
}
