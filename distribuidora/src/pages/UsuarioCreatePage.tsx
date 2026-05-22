import { useState } from 'react';
import { Alert, Box, Button, Card, CardContent, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { createUsuario } from '../services/usuarioService';
import { extractValidationErrors } from '../services/validation';

export function UsuarioCreatePage() {
  const navigate = useNavigate();
  const { session } = useAuth();

  const [tipoDocumento, setTipoDocumento] = useState('DNI');
  const [numeroDocumento, setNumeroDocumento] = useState('');
  const [nombres, setNombres] = useState('');
  const [apellidos, setApellidos] = useState('');
  const [nombreUsuario, setNombreUsuario] = useState('');
  const [password, setPassword] = useState('');
  const [rolCodigo, setRolCodigo] = useState<'ROLE_ADMIN' | 'ROLE_VENDEDOR'>('ROLE_VENDEDOR');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');
    setFieldErrors({});

    if (!session?.empresaId) {
      setError('No se pudo determinar la empresa de la sesión.');
      return;
    }

    setIsSubmitting(true);
    try {
      await createUsuario({
        empresaId: session.empresaId,
        tipoDocumento,
        numeroDocumento,
        nombres,
        apellidos,
        nombreUsuario,
        password,
        rolCodigo
      });
      navigate('/usuarios', { replace: true });
    } catch (err) {
      const mapped = extractValidationErrors(err);
      if (Object.keys(mapped).length > 0) {
        setFieldErrors(mapped);
      } else {
        setError('No se pudo crear el usuario. Verifica los datos e intenta nuevamente.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Box>
      <Card>
        <CardContent sx={{ p: 3 }}>
          <Stack spacing={2.5} component="form" onSubmit={handleSubmit}>
            <Stack spacing={0.5}>
              <Typography variant="h5">Nuevo Usuario</Typography>
              <Typography variant="body2" color="text.secondary">
                Crea usuarios administradores o vendedores.
              </Typography>
            </Stack>

            {error && <Alert severity="error">{error}</Alert>}

            <TextField
              select
              label="Tipo de Documento"
              value={tipoDocumento}
              onChange={(e) => setTipoDocumento(e.target.value)}
              error={Boolean(fieldErrors.tipoDocumento)}
              helperText={fieldErrors.tipoDocumento}
              required
            >
              <MenuItem value="DNI">DNI</MenuItem>
              <MenuItem value="RUC">RUC</MenuItem>
              <MenuItem value="CE">CE</MenuItem>
              <MenuItem value="PASAPORTE">Pasaporte</MenuItem>
            </TextField>

            <TextField
              label="Número de Documento"
              value={numeroDocumento}
              onChange={(e) => setNumeroDocumento(e.target.value)}
              error={Boolean(fieldErrors.numeroDocumento)}
              helperText={fieldErrors.numeroDocumento}
              required
            />
            <TextField
              label="Nombres"
              value={nombres}
              onChange={(e) => setNombres(e.target.value)}
              error={Boolean(fieldErrors.nombres)}
              helperText={fieldErrors.nombres}
              required
            />
            <TextField
              label="Apellidos"
              value={apellidos}
              onChange={(e) => setApellidos(e.target.value)}
              error={Boolean(fieldErrors.apellidos)}
              helperText={fieldErrors.apellidos}
              required
            />
            <TextField
              label="Nombre de Usuario"
              value={nombreUsuario}
              onChange={(e) => setNombreUsuario(e.target.value)}
              error={Boolean(fieldErrors.nombreUsuario)}
              helperText={fieldErrors.nombreUsuario}
              required
            />
            <TextField
              label="Contraseña"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              error={Boolean(fieldErrors.password)}
              helperText={fieldErrors.password}
              required
            />

            <TextField
              select
              label="Rol"
              value={rolCodigo}
              onChange={(e) => setRolCodigo(e.target.value as 'ROLE_ADMIN' | 'ROLE_VENDEDOR')}
              error={Boolean(fieldErrors.rolCodigo)}
              helperText={fieldErrors.rolCodigo}
              required
            >
              <MenuItem value="ROLE_ADMIN">Administrador</MenuItem>
              <MenuItem value="ROLE_VENDEDOR">Vendedor</MenuItem>
            </TextField>

            <Button type="submit" variant="contained" disabled={isSubmitting}>
              {isSubmitting ? 'Guardando...' : 'Crear Usuario'}
            </Button>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
