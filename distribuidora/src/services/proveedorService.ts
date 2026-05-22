import api from './http';
import { consultarDocumentoExterno } from './consultaExternaService';
import type {
  PageResponse,
  ProveedorCreatePayload,
  ProveedorResponse,
  ProveedorUpdatePayload,
  TipoDocumentoCatalogo,
  UbigeoDistritoOption,
  UbigeoUbicacion
} from '../types/proveedor';

export { consultarDocumentoExterno };

export async function listTiposDocumentoProveedor() {
  const { data } = await api.get<TipoDocumentoCatalogo[]>('/api/proveedores/catalogos/tipos-documento');
  return data;
}

export async function listUbigeoDepartamentos() {
  const { data } = await api.get<string[]>('/api/proveedores/catalogos/ubigeo/departamentos');
  return data;
}

export async function listUbigeoProvincias(departamento: string) {
  const { data } = await api.get<string[]>('/api/proveedores/catalogos/ubigeo/provincias', {
    params: { departamento }
  });
  return data;
}

export async function listUbigeoDistritos(departamento: string, provincia: string) {
  const { data } = await api.get<UbigeoDistritoOption[]>('/api/proveedores/catalogos/ubigeo/distritos', {
    params: { departamento, provincia }
  });
  return data;
}

export async function getUbigeoResumen(codigo: string) {
  const { data } = await api.get<UbigeoUbicacion>('/api/proveedores/catalogos/ubigeo/resumen', {
    params: { codigo }
  });
  return data;
}

export async function listProveedores(empresaId: number, page = 0, size = 20) {
  const { data } = await api.get<PageResponse<ProveedorResponse>>('/api/proveedores', {
    params: { empresaId, page, size, sort: 'id,desc' }
  });
  return data;
}

export async function getProveedorById(id: number) {
  const { data } = await api.get<ProveedorResponse>(`/api/proveedores/${id}`);
  return data;
}

export async function createProveedor(payload: ProveedorCreatePayload) {
  const { data } = await api.post<ProveedorResponse>('/api/proveedores', payload);
  return data;
}

export async function updateProveedor(id: number, payload: ProveedorUpdatePayload) {
  const { data } = await api.put<ProveedorResponse>(`/api/proveedores/${id}`, payload);
  return data;
}

export async function deleteProveedor(id: number) {
  await api.delete(`/api/proveedores/${id}`);
}

export async function toggleEstadoProveedor(id: number) {
  const { data } = await api.patch<ProveedorResponse>(`/api/proveedores/${id}/toggle-estado`);
  return data;
}
