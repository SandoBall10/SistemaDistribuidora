import api from './http';
import type { VentaCreatePayload, VentaResponse } from '../types/venta';

export async function createVenta(payload: VentaCreatePayload) {
  const { data } = await api.post<VentaResponse>('/api/ventas', payload);
  return data;
}
