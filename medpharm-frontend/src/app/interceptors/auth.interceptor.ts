import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { API_URL } from '../app.constants';
import { AuthService } from '../services/auth.service';

/**
 * Clona cada petición saliente hacia la API e inyecta la cabecera
 * "Authorization: Bearer <token>" cuando existe una sesión activa.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  const esPeticionDeApi = req.url.startsWith(API_URL);
  const esLogin = req.url.startsWith(`${API_URL}/auth/`);

  const peticion =
    token && esPeticionDeApi && !esLogin
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      // Token vencido o inválido: se cierra la sesión y se envía al login
      if (error.status === 401 && token && !esLogin) {
        authService.logout();
      }
      return throwError(() => error);
    }),
  );
};
