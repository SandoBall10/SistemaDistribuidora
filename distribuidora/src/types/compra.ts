export interface CompraDetalleCreatePayload {
  productoId: number;
  cantidad: number;
  precioUnitario: number;
  loteCodigo: string;
  fechaProduccion?: string | null;
  fechaVencimiento?: string | null;
}

export interface CompraCreatePayload {
  proveedorId: number;
  fechaIngreso: string;
  almacen?: string | null;
  tipoComprobanteCodigo: string;
  numeroComprobante: string;
  monedaCodigo: string;
  detalles: CompraDetalleCreatePayload[];
}

export interface CompraResponse {
  id: number;
  empresaId: number;
  proveedorId: number;
  fechaIngreso: string;
  almacen: string | null;
  tipoComprobanteCodigo: string;
  numeroComprobante: string;
  monedaCodigo: string;
  fechaCreacion: string;
}
