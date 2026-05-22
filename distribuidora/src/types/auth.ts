export interface LoginPayload {
  empresaId: number;
  nombreUsuario: string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;
  accessExpiresInSeconds: number;
  refreshToken: string;
  tokenType: string;
}

export interface AuthSession extends TokenResponse {
  empresaId: number;
  nombreUsuario: string;
  rol: string | null;
}
