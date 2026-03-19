import api from './http';
import type { LoginPayload, TokenResponse } from '../types/auth';

export async function loginRequest(payload: LoginPayload) {
  const { data } = await api.post<TokenResponse>('/api/auth/login', payload);
  return data;
}

export async function refreshRequest(refreshToken: string) {
  const { data } = await api.post<TokenResponse>('/api/auth/refresh', { refreshToken });
  return data;
}
