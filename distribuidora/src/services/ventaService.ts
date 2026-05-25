import api from './http';
import type { VentaComprobante, VentaCreatePayload, VentaResponse } from '../types/venta';

export async function createVenta(payload: VentaCreatePayload) {
  const { data } = await api.post<VentaResponse>('/api/ventas', payload);
  return data;
}

export async function getVentaComprobante(id: number) {
  const { data } = await api.get<VentaComprobante>(`/api/ventas/${id}`);
  return data;
}

export async function listVentas() {
  const { data } = await api.get<VentaResponse[]>('/api/ventas');
  return data;
}
