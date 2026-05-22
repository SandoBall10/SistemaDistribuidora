export interface ClienteResponse {
  id: number;
  empresaId: number;
  codigoCliente: string;
  numeroDocumento?: string | null;
  razonSocialNombre?: string | null;
  direccion?: string | null;
  tipoDocumentoId?: number | null;
  activo: boolean;
}

export interface ClienteCreatePayload {
  tipoDocumento: 'DNI' | 'RUC';
  numeroDocumento: string;
  razonSocialNombre: string;
  direccion?: string | null;
  nombres?: string | null;
  apellidoPaterno?: string | null;
  apellidoMaterno?: string | null;
  estadoSunat?: string | null;
  condicionSunat?: string | null;
}
