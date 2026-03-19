import axios, { AxiosError } from 'axios';
import type { InternalAxiosRequestConfig } from 'axios';

interface RefreshResult {
  accessToken: string;
}

interface InterceptorHandlers {
  getAccessToken: () => string | null;
  refreshSession: () => Promise<RefreshResult | null>;
  logout: () => void;
}

interface RetryConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
  timeout: 12000,
  headers: {
    'Content-Type': 'application/json'
  }
});

export function setupApiInterceptors(handlers: InterceptorHandlers) {
  const requestId = api.interceptors.request.use((config) => {
    const token = handlers.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  const responseId = api.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
      const originalConfig = error.config as RetryConfig | undefined;
      if (!originalConfig || originalConfig._retry) {
        return Promise.reject(error);
      }

      if (error.response?.status === 401) {
        originalConfig._retry = true;
        const refreshed = await handlers.refreshSession();

        if (refreshed?.accessToken) {
          originalConfig.headers = originalConfig.headers ?? {};
          originalConfig.headers.Authorization = `Bearer ${refreshed.accessToken}`;
          return api(originalConfig);
        }

        handlers.logout();
      }

      return Promise.reject(error);
    }
  );

  return () => {
    api.interceptors.request.eject(requestId);
    api.interceptors.response.eject(responseId);
  };
}

export default api;
