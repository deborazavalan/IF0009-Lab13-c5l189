export type EstadoReceta = 'PENDIENTE' | 'DESPACHADA' | 'CANCELADA';

export interface DetalleReceta {
  id: number;
  medicamentoId: number;
  medicamentoNombre: string;
  cantidad: number;
  dosisIndicada: string | null;
}

export interface Receta {
  id: number;
  codigoReceta: string;
  pacienteNombre: string;
  medicoNombre: string;
  estado: EstadoReceta;
  fechaEmision: string;
  detalles: DetalleReceta[];
}

export interface DetalleRecetaRequest {
  medicamentoId: number;
  cantidad: number;
  dosisIndicada: string;
}

export interface RecetaRequest {
  pacienteNombre: string;
  detalles: DetalleRecetaRequest[];
}
