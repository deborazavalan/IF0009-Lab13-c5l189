import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador personalizado: la cantidad prescrita debe ser un número entero
 * estrictamente mayor a 0. Un valor vacío lo cubre Validators.required.
 */
export const positivoValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const valor = control.value;

  if (valor === null || valor === undefined || valor === '') {
    return null;
  }

  const numero = Number(valor);
  return Number.isInteger(numero) && numero > 0 ? null : { positivo: true };
};
