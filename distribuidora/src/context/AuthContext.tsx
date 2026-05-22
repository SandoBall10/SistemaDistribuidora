import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { loginRequest, refreshRequest } from '../services/authService';
import { clearSession, getSession, saveSession } from '../services/storage';
import { setupApiInterceptors } from '../services/http';
import { extractRoleFromAccessToken } from '../services/jwt';
import type { AuthSession, LoginPayload } from '../types/auth';

interface AuthContextValue {
  session: AuthSession | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (payload: LoginPayload) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const logout = useCallback(() => {
    setSession(null);
    clearSession();
  }, []);

  const refreshSession = useCallback(async () => {
    if (!session?.refreshToken) {
      return null;
    }

    try {
      const refreshed = await refreshRequest(session.refreshToken);
      const nextSession: AuthSession = {
        ...refreshed,
        empresaId: session.empresaId,
        nombreUsuario: session.nombreUsuario,
        rol: extractRoleFromAccessToken(refreshed.accessToken)
      };
      setSession(nextSession);
      saveSession(nextSession);
      return { accessToken: refreshed.accessToken };
    } catch {
      logout();
      return null;
    }
  }, [logout, session]);

  useEffect(() => {
    const initial = getSession();
    if (initial) {
      setSession(initial);
    }
    setIsLoading(false);
  }, []);

  useEffect(() => {
    const cleanup = setupApiInterceptors({
      getAccessToken: () => session?.accessToken ?? null,
      refreshSession,
      logout
    });
    return cleanup;
  }, [logout, refreshSession, session]);

  const login = useCallback(async (payload: LoginPayload) => {
    const response = await loginRequest(payload);
    const nextSession: AuthSession = {
      ...response,
      empresaId: payload.empresaId,
      nombreUsuario: payload.nombreUsuario,
      rol: extractRoleFromAccessToken(response.accessToken)
    };
    setSession(nextSession);
    saveSession(nextSession);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      isAuthenticated: Boolean(session?.accessToken),
      isLoading,
      login,
      logout
    }),
    [isLoading, login, logout, session]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuthContext() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuthContext debe usarse dentro de AuthProvider');
  }
  return context;
}
