import api from './http';
import type { CompraCreatePayload, CompraResponse } from '../types/compra';

export async function createCompra(payload: CompraCreatePayload) {
  const { data } = await api.post<CompraResponse>('/api/compras', payload);
  return data;
}

// Trae el historial de compras de la empresa del usuario logueado
export const listarCompras = async (): Promise<CompraResponse[]> => {
  const { data } = await api.get<CompraResponse[]>('/api/compras');
  return data;
};

// Trae el detalle de una compra específica
export const obtenerCompra = async (id: number): Promise<CompraResponse> => {
  const { data } = await api.get<CompraResponse>(`/api/compras/${id}`);
  return data;
};
