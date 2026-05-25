export interface CatalogoOption {
  id: number;
  nombre: string;
}

export interface IgvCatalogoItem {
  id: number;
  nombre: string;
  porcentaje: number;
}

export interface ProductoCreatePayload {
  empresaId: number;
  codigo: string;
  nombre: string;
  descripcion?: string;
  claseProductoId: number;
  unidadMedidaBaseId: number;
  tipoIgvId: number;
  precioVenta?: number;
}

export interface ProductoUpdatePayload {
  nombre: string;
  descripcion?: string;
  claseProductoId: number;
  unidadMedidaBaseId: number;
  tipoIgvId: number;
  precioVenta?: number;
}

export interface ProductoResponse {
  id: number;
  empresaId: number;
  codigo: string;
  nombre: string;
  descripcion?: string | null;
  claseProductoId: number;
  claseProductoNombre?: string | null;
  unidadMedidaBaseId: number;
  unidadMedidaNombre?: string | null;
  tipoIgvId?: number | null;
  tipoIgvCodigo?: string | null;
  tipoIgvNombre?: string | null;
  tipoIgvPorcentaje?: number | null;
  precioVenta?: number | null;
  activo: boolean;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
