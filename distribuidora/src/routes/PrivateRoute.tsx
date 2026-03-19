import { Navigate, Outlet } from 'react-router-dom';
import { CircularProgress, Stack } from '@mui/material';
import { useAuth } from '../hooks/useAuth';

export function PrivateRoute() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <Stack minHeight="100vh" alignItems="center" justifyContent="center">
        <CircularProgress />
      </Stack>
    );
  }

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
}
