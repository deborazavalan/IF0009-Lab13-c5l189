import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

import { API_URL } from '../app.constants';
import { AuthResponse, LoginRequest, Rol } from '../models/auth.model';

interface Sesion {
  token: string;
  username: string;
  rol: Rol;
}

const CLAVE_SESION = 'medpharm_sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly _sesion = signal<Sesion | null>(this.leerSesion());

  readonly sesion = this._sesion.asReadonly();
  readonly estaAutenticado = computed(() => !!this._sesion()?.token);
  readonly username = computed(() => this._sesion()?.username ?? '');
  readonly rol = computed(() => this._sesion()?.rol ?? null);

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_URL}/auth/login`, credentials).pipe(
      tap((respuesta) => {
        const sesion: Sesion = {
          token: respuesta.token,
          username: respuesta.username,
          rol: respuesta.rol,
        };
        localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
        this._sesion.set(sesion);
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(CLAVE_SESION);
    this._sesion.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this._sesion()?.token ?? null;
  }

  private leerSesion(): Sesion | null {
    try {
      const guardada = localStorage.getItem(CLAVE_SESION);
      return guardada ? (JSON.parse(guardada) as Sesion) : null;
    } catch {
      return null;
    }
  }
}
