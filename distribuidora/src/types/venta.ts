export interface VentaDetalleCreatePayload {
  productoId: number;
  cantidad: number;
  precioUnitario: number;
  loteCodigo: string;
}

export interface VentaCreatePayload {
  clienteId: number;
  fechaEmision: string;
  tipoComprobanteCodigo: string;
  serie: string;
  numeroComprobante: string;
  monedaCodigo: string;
  totalGravado: number;
  totalIgv: number;
  totalVenta: number;
  detalles: VentaDetalleCreatePayload[];
}

export interface VentaResponse {
  id: number;
  empresaId: number;
  clienteId: number;
  fechaEmision: string;
  tipoComprobanteCodigo: string;
  serie: string;
  numeroComprobante: string;
  monedaCodigo: string;
  totalGravado: number;
  totalIgv: number;
  totalVenta: number;
  fechaCreacion: string;
}
