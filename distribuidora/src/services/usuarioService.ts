import api from './http';
import type { PageResponse, UsuarioCreatePayload, UsuarioResponse, UsuarioUpdatePayload } from '../types/usuario';

export async function listUsuarios(page = 0, size = 10) {
  const { data } = await api.get<PageResponse<UsuarioResponse>>('/api/usuarios', {
    params: { page, size, sort: 'id,desc' }
  });
  return data;
}

export async function createUsuario(payload: UsuarioCreatePayload) {
  const { data } = await api.post<UsuarioResponse>('/api/usuarios', payload);
  return data;
}

export async function getUsuarioById(id: number) {
  const { data } = await api.get<UsuarioResponse>(`/api/usuarios/${id}`);
  return data;
}

export async function updateUsuario(id: number, payload: UsuarioUpdatePayload) {
  const { data } = await api.put<UsuarioResponse>(`/api/usuarios/${id}`, payload);
  return data;
}

export async function deleteUsuario(id: number) {
  await api.delete(`/api/usuarios/${id}`);
}

export async function toggleEstadoUsuario(id: number) {
  const { data } = await api.patch<UsuarioResponse>(`/api/usuarios/${id}/toggle-estado`);
  return data;
}
