import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly cargando = signal(false);
  protected readonly errorMensaje = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  ngOnInit(): void {
    // Si ya hay sesión activa, no tiene sentido mostrar el login
    if (this.auth.estaAutenticado()) {
      this.router.navigate(['/recetas']);
    }
  }

  protected enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.errorMensaje.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.cargando.set(false);
        this.router.navigate(['/recetas']);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.errorMensaje.set(
          error.status === 0
            ? 'No se pudo conectar con el servidor. ¿Está corriendo el backend en el puerto 8080?'
            : error.status === 401
              ? 'Usuario o contraseña incorrectos.'
              : (error.error?.detail ?? 'No fue posible iniciar sesión.'),
        );
      },
    });
  }

  protected campoInvalido(nombre: 'username' | 'password'): boolean {
    const control = this.form.controls[nombre];
    return control.invalid && (control.touched || control.dirty);
  }
}
