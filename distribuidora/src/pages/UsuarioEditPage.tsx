import { useEffect, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import { getUsuarioById, updateUsuario } from '../services/usuarioService';
import { extractValidationErrors } from '../services/validation';

function splitFullName(value: string | null): { nombres: string; apellidos: string } {
  if (!value) {
    return { nombres: '', apellidos: '' };
  }
  const parts = value.trim().split(/\s+/);
  if (parts.length <= 1) {
    return { nombres: value, apellidos: '' };
  }
  return {
    nombres: parts.slice(0, -1).join(' '),
    apellidos: parts.slice(-1).join('')
  };
}

export function UsuarioEditPage() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();

  const [numeroDocumento, setNumeroDocumento] = useState('');
  const [nombres, setNombres] = useState('');
  const [apellidos, setApellidos] = useState('');
  const [rolCodigo, setRolCodigo] = useState<'ROLE_ADMIN' | 'ROLE_VENDEDOR'>('ROLE_VENDEDOR');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [nombreUsuario, setNombreUsuario] = useState('');

  useEffect(() => {
    async function load() {
      if (!id) {
        setError('ID de usuario inválido.');
        setIsLoading(false);
        return;
      }
      try {
        const data = await getUsuarioById(Number(id));
        const fullName = splitFullName(data.personaNombreCompleto);
        setNumeroDocumento(data.numeroDocumento ?? '');
        setNombres(fullName.nombres);
        setApellidos(fullName.apellidos);
        setRolCodigo((data.rolCodigo as 'ROLE_ADMIN' | 'ROLE_VENDEDOR') ?? 'ROLE_VENDEDOR');
        setNombreUsuario(data.nombreUsuario);
      } catch {
        setError('No se pudo cargar el usuario.');
      } finally {
        setIsLoading(false);
      }
    }

    void load();
  }, [id]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!id) {
      setError('ID de usuario inválido.');
      return;
    }
    setError('');
    setFieldErrors({});
    setIsSubmitting(true);
    try {
      await updateUsuario(Number(id), { numeroDocumento, nombres, apellidos, rolCodigo });
      navigate('/usuarios', { replace: true });
    } catch (err) {
      const mapped = extractValidationErrors(err);
      if (Object.keys(mapped).length > 0) {
        setFieldErrors(mapped);
      } else {
        setError('No se pudo actualizar el usuario.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return <Typography>Cargando...</Typography>;
  }

  return (
    <Box>
      <Card>
        <CardContent sx={{ p: 3 }}>
          <Stack spacing={2.5} component="form" onSubmit={handleSubmit}>
            <Stack spacing={0.5}>
              <Typography variant="h5">Editar Usuario</Typography>
              <Typography variant="body2" color="text.secondary">
                Actualiza nombres, apellidos y rol del usuario.
              </Typography>
            </Stack>

            {error && <Alert severity="error">{error}</Alert>}

            <TextField label="Nombre de Usuario" value={nombreUsuario} disabled />
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
              {isSubmitting ? 'Guardando...' : 'Guardar Cambios'}
            </Button>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
