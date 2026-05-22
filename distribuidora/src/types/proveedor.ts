import type { PageResponse } from './producto';

export type { PageResponse };

export interface TipoDocumentoCatalogo {
  id: number;
  nombre: string;
  codigoSunat: string;
}

export interface UbigeoDistritoOption {
  codigoUbigeo: string;
  nombre: string;
}

export interface UbigeoUbicacion {
  codigoUbigeo: string;
  departamento: string;
  provincia: string;
  distrito: string;
}

export interface ProveedorResponse {
  id: number;
  empresaId: number;
  personaId?: number | null;
  codigoProveedor: string;
  tipoPersonaId?: number | null;
  tipoDocumentoId?: number | null;
  tipoDocumentoNombre?: string | null;
  numeroDocumento?: string | null;
  nombres?: string | null;
  apellidoPaterno?: string | null;
  apellidoMaterno?: string | null;
  razonSocial?: string | null;
  razonSocialNombre?: string | null;
  direccion?: string | null;
  email?: string | null;
  telefono?: string | null;
  nombreComercial?: string | null;
  estadoSunat?: string | null;
  condicionSunat?: string | null;
  genero?: string | null;
  esContribuyente?: boolean | null;
  ubigeoId?: string | null;
  plazoCreditoDias?: number | null;
  cuentaSoles?: string | null;
  activo: boolean;
}

export interface ProveedorCreatePayload {
  empresaId: number;
  tipoPersonaId: number;
  tipoDocumentoId: number;
  numeroDocumento: string;
  nombres?: string;
  apellidoPaterno?: string;
  apellidoMaterno?: string;
  razonSocial?: string;
  direccion?: string;
  email?: string;
  telefono?: string;
  nombreComercial?: string;
  estadoSunat?: string;
  condicionSunat?: string;
  genero?: string;
  esContribuyente?: boolean;
  ubigeoId?: string;
  plazoCreditoDias?: number | null;
  cuentaSoles?: string;
}

export interface ProveedorUpdatePayload {
  tipoPersonaId: number;
  tipoDocumentoId: number;
  numeroDocumento: string;
  nombres?: string;
  apellidoPaterno?: string;
  apellidoMaterno?: string;
  razonSocial?: string;
  direccion?: string;
  email?: string;
  telefono?: string;
  nombreComercial?: string;
  estadoSunat?: string;
  condicionSunat?: string;
  genero?: string;
  esContribuyente?: boolean;
  ubigeoId?: string;
  plazoCreditoDias?: number | null;
  cuentaSoles?: string;
}

export const TIPO_PERSONA_NATURAL = 1;
export const TIPO_PERSONA_JURIDICA = 2;
