import api from './http';
import type {
  CatalogoOption,
  IgvCatalogoItem,
  PageResponse,
  ProductoCreatePayload,
  ProductoResponse,
  ProductoUpdatePayload
} from '../types/producto';

export async function listProductos(empresaId: number, page = 0, size = 20) {
  const { data } = await api.get<PageResponse<ProductoResponse>>('/api/productos', {
    params: { empresaId, page, size, sort: 'id,desc' }
  });
  return data;
}

export async function getProductoById(id: number) {
  const { data } = await api.get<ProductoResponse>(`/api/productos/${id}`);
  return data;
}

export async function createProducto(payload: ProductoCreatePayload) {
  const { data } = await api.post<ProductoResponse>('/api/productos', payload);
  return data;
}

export async function updateProducto(id: number, payload: ProductoUpdatePayload) {
  const { data } = await api.put<ProductoResponse>(`/api/productos/${id}`, payload);
  return data;
}

export async function deleteProducto(id: number) {
  await api.delete(`/api/productos/${id}`);
}

export async function toggleEstadoProducto(id: number) {
  const { data } = await api.patch<ProductoResponse>(`/api/productos/${id}/toggle-estado`);
  return data;
}

export async function listClasesProducto(empresaId: number) {
  const { data } = await api.get<CatalogoOption[]>('/api/productos/catalogos/clases', { params: { empresaId } });
  return data;
}

export async function listUnidadesMedida() {
  const { data } = await api.get<CatalogoOption[]>('/api/productos/catalogos/unidades');
  return data;
}

export async function listIgvCatalogo() {
  const { data } = await api.get<IgvCatalogoItem[]>('/api/productos/catalogos/igv');
  return data;
}
