import { AxiosError } from 'axios';

interface ValidationFieldError {
  campo: string;
  mensaje: string;
}

export function extractValidationErrors(error: unknown): Record<string, string> {
  const axiosError = error as AxiosError<ValidationFieldError[]>;
  const payload = axiosError.response?.data;
  if (!Array.isArray(payload)) {
    return {};
  }

  return payload.reduce<Record<string, string>>((acc, item) => {
    if (item?.campo && item?.mensaje) {
      acc[item.campo] = item.mensaje;
    }
    return acc;
  }, {});
}
