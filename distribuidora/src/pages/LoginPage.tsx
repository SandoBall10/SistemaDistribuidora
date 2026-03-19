import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Container,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import { useAuth } from '../hooks/useAuth';

export function LoginPage() {
  const navigate = useNavigate();
  const { isAuthenticated, login } = useAuth();
  const [empresaId, setEmpresaId] = useState(1);
  const [nombreUsuario, setNombreUsuario] = useState('');
  const [password, setPassword] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');
    setIsSubmitting(true);

    try {
      await login({ empresaId, nombreUsuario, password });
      navigate('/dashboard', { replace: true });
    } catch {
      setError('No se pudo iniciar sesión. Verifica tus credenciales e intenta nuevamente.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Box
      minHeight="100vh"
      display="flex"
      alignItems="center"
      bgcolor="background.default"
      sx={{ background: 'linear-gradient(180deg, #f6f8ff 0%, #eef3fb 100%)' }}
    >
      <Container maxWidth="sm">
        <Card>
          <CardContent sx={{ p: 4 }}>
            <Stack component="form" spacing={2.5} onSubmit={handleSubmit}>
              <Typography variant="h4">Iniciar sesión</Typography>
              <Typography variant="body2" color="text.secondary">
                Accede al ERP de Distribuidora con tus credenciales corporativas.
              </Typography>
              {error && <Alert severity="error">{error}</Alert>}
              <TextField
                type="number"
                label="Empresa ID"
                value={empresaId}
                onChange={(event) => setEmpresaId(Number(event.target.value))}
                fullWidth
                required
              />
              <TextField
                label="Usuario"
                value={nombreUsuario}
                onChange={(event) => setNombreUsuario(event.target.value)}
                fullWidth
                required
              />
              <TextField
                type="password"
                label="Contraseña"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                fullWidth
                required
              />
              <Button type="submit" variant="contained" size="large" disabled={isSubmitting}>
                {isSubmitting ? 'Validando...' : 'Ingresar'}
              </Button>
            </Stack>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
}
