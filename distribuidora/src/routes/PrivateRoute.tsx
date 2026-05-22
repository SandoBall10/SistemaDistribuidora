import { Navigate, Outlet } from 'react-router-dom';
import { CircularProgress, Stack } from '@mui/material';
import { useAuth } from '../hooks/useAuth';

interface PrivateRouteProps {
  allowedRoles?: string[];
}

export function PrivateRoute({ allowedRoles }: PrivateRouteProps) {
  const { isAuthenticated, isLoading, session } = useAuth();

  if (isLoading) {
    return (
      <Stack minHeight="100vh" alignItems="center" justifyContent="center">
        <CircularProgress />
      </Stack>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles?.length) {
    const currentRole = session?.rol;
    if (!currentRole || !allowedRoles.includes(currentRole)) {
      return <Navigate to="/dashboard" replace />;
    }
  }

  return <Outlet />;
}
