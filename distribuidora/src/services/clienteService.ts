import api from './http';
import type { ClienteCreatePayload, ClienteResponse } from '../types/cliente';

export async function listClientesActivos() {
  const { data } = await api.get<ClienteResponse[]>('/api/clientes');
  return data;
}

export async function createCliente(payload: ClienteCreatePayload) {
  const { data } = await api.post<ClienteResponse>('/api/clientes', payload);
  return data;
}
