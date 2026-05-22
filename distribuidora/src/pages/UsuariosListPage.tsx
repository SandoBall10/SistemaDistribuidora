import { useEffect, useState } from 'react';
import {
  Alert,
  Button,
  Card,
  CardContent,
  Chip,
  IconButton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Tooltip,
  Typography
} from '@mui/material';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import { Link as RouterLink } from 'react-router-dom';
import { toggleEstadoUsuario, listUsuarios } from '../services/usuarioService';
import type { UsuarioResponse } from '../types/usuario';

export function UsuariosListPage() {
  const [rows, setRows] = useState<UsuarioResponse[]>([]);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  async function loadUsers() {
    setIsLoading(true);
    setError('');
    try {
      const data = await listUsuarios(0, 50);
      setRows(data.content);
    } catch {
      setError('No se pudo cargar la lista de usuarios.');
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadUsers();
  }, []);

  const handleToggleEstado = async (id: number, activo: boolean) => {
    try {
      await toggleEstadoUsuario(id);
      await loadUsers();
    } catch {
      setError(activo ? 'No se pudo desactivar el usuario.' : 'No se pudo activar el usuario.');
    }
  };

  return (
    <Stack spacing={2}>
      <Stack spacing={0.5}>
        <Typography variant="h4">Gestión de Usuarios</Typography>
        <Typography variant="body2" color="text.secondary">
          Lista de usuarios registrados en la empresa.
        </Typography>
      </Stack>
      <Button component={RouterLink} to="/usuarios/nuevo" variant="contained" sx={{ alignSelf: 'flex-start' }}>
        Nuevo Usuario
      </Button>

      {error && <Alert severity="error">{error}</Alert>}

      <Card>
        <CardContent>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Usuario</TableCell>
                <TableCell>Persona</TableCell>
                <TableCell>Documento</TableCell>
                <TableCell>Rol</TableCell>
                <TableCell>Estado</TableCell>
                <TableCell align="right">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map((row) => (
                <TableRow key={row.id} hover>
                  <TableCell>{row.nombreUsuario}</TableCell>
                  <TableCell>{row.personaNombreCompleto ?? '-'}</TableCell>
                  <TableCell>
                    {row.tipoDocumento ?? '-'} {row.numeroDocumento ?? ''}
                  </TableCell>
                  <TableCell>{row.rolCodigo ?? '-'}</TableCell>
                  <TableCell>
                    <Chip size="small" label={row.activo ? 'Activo' : 'Inactivo'} color={row.activo ? 'success' : 'default'} />
                  </TableCell>
                  <TableCell align="right">
                    {row.activo && (
                      <Tooltip title="Editar">
                        <IconButton
                          size="small"
                          component={RouterLink}
                          to={`/usuarios/${row.id}/editar`}
                          color="primary"
                        >
                          <EditOutlinedIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    )}

                    {row.activo ? (
                      <Tooltip title="Desactivar">
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => handleToggleEstado(row.id, row.activo)}
                        >
                          <DeleteOutlineIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    ) : (
                      <Tooltip title="Activar">
                        <IconButton
                          size="small"
                          color="success"
                          onClick={() => handleToggleEstado(row.id, row.activo)}
                        >
                          <CheckCircleOutlineIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    )}
                  </TableCell>
                </TableRow>
              ))}
              {!rows.length && !isLoading && (
                <TableRow>
                  <TableCell colSpan={6}>
                    <Typography variant="body2" color="text.secondary">
                      No hay usuarios registrados.
                    </Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </Stack>
  );
}
