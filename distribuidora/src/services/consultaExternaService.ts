import { isAxiosError } from 'axios';
import api from './http';
import type { ConsultaDocumentoExternoResponse } from '../types/consultaDocumento';

export async function consultarDocumentoExterno(tipo: 'DNI' | 'RUC', numero: string) {
  const numeroLimpio = numero.replace(/\D/g, '');
  const { data } = await api.get<ConsultaDocumentoExternoResponse>('/api/externo/consultar-documento', {
    params: { tipo, numero: numeroLimpio }
  });
  return data;
}

/** Mensaje amigable según código HTTP de la consulta RENIEC/SUNAT. */
export function mensajeErrorConsultaDocumento(error: unknown): string {
  if (isAxiosError(error)) {
    const status = error.response?.status;
    const body = error.response?.data as { message?: string } | undefined;
    const backendMsg = body?.message?.trim();

    if (status === 403) {
      return 'Error de API: Token inválido o límite de consultas excedido.';
    }
    if (status === 404) {
      return backendMsg || 'Documento no encontrado en SUNAT/RENIEC.';
    }
    if (status === 500) {
      return backendMsg || 'Error interno del servidor.';
    }
    if (status === 400 && backendMsg) {
      return backendMsg;
    }
    if (backendMsg) {
      return backendMsg;
    }
    if (status) {
      return `Consulta rechazada (HTTP ${status}).`;
    }
    if (error.code === 'ERR_NETWORK') {
      return 'No se pudo conectar con el servidor. Verifique que el backend esté en ejecución.';
    }
  }
  return 'Error al consultar el documento.';
}
