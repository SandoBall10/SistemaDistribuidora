import api from './http';
import type { LoteDisponible } from '../types/lote';

export async function listLotesDisponibles(productoId: number): Promise<LoteDisponible[]> {
  const { data } = await api.get<LoteDisponible[]>('/api/lotes/disponibles', {
    params: { productoId }
  });
  return data;
}
