export interface UsuarioCreatePayload {
  empresaId: number;
  tipoDocumento: string;
  numeroDocumento: string;
  nombres: string;
  apellidos: string;
  nombreUsuario: string;
  password: string;
  rolCodigo: 'ROLE_ADMIN' | 'ROLE_VENDEDOR';
}

export interface UsuarioUpdatePayload {
  numeroDocumento: string;
  nombres: string;
  apellidos: string;
  rolCodigo: 'ROLE_ADMIN' | 'ROLE_VENDEDOR';
}

export interface UsuarioResponse {
  id: number;
  empresaId: number;
  nombreUsuario: string;
  rolCodigo: string | null;
  personaNombreCompleto: string | null;
  tipoDocumento: string | null;
  numeroDocumento: string | null;
  activo: boolean;
}

export interface ValidationFieldError {
  campo: string;
  mensaje: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
