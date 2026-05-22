/** Respuesta unificada de GET /api/externo/consultar-documento */
export interface ConsultaDocumentoExternoResponse {
  tipo: string;
  nombres?: string | null;
  apellidoPaterno?: string | null;
  apellidoMaterno?: string | null;
  razonSocial?: string | null;
  nombreComercial?: string | null;
  direccion?: string | null;
  ubigeoCodigo?: string | null;
  departamento?: string | null;
  provincia?: string | null;
  distrito?: string | null;
  estadoSunat?: string | null;
  condicionSunat?: string | null;
}
