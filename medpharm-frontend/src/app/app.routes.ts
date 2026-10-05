import { Routes } from '@angular/router';

import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'recetas' },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'recetas',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/recetas-list/recetas-list.component').then((m) => m.RecetasListComponent),
  },
  {
    path: 'nueva-receta',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/receta-form/receta-form.component').then((m) => m.RecetaFormComponent),
  },
  { path: '**', redirectTo: 'recetas' },
];
