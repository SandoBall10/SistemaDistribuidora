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
import { useAuth } from '../hooks/useAuth';
import { listProveedores, toggleEstadoProveedor } from '../services/proveedorService';
import type { ProveedorResponse } from '../types/proveedor';

export function ProveedoresListPage() {
  const { session } = useAuth();
  const [rows, setRows] = useState<ProveedorResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  async function loadRows() {
    if (!session?.empresaId) {
      setError('No se pudo determinar la empresa de la sesión.');
      setIsLoading(false);
      return;
    }
    setIsLoading(true);
    setError('');
    try {
      const data = await listProveedores(session.empresaId, 0, 100);
      setRows(data.content);
    } catch {
      setError('No se pudo cargar la lista de proveedores.');
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadRows();
  }, [session?.empresaId]);

  const handleToggleEstado = async (id: number, activo: boolean) => {
    try {
      await toggleEstadoProveedor(id);
      await loadRows();
    } catch {
      setError(activo ? 'No se pudo desactivar el proveedor.' : 'No se pudo activar el proveedor.');
    }
  };

  const docLabel = (row: ProveedorResponse) => {
    const pref = row.tipoDocumentoNombre ?? '';
    const num = row.numeroDocumento ?? '';
    const s = `${pref} ${num}`.trim();
    return s || '-';
  };

  return (
    <Stack spacing={2}>
      <Stack spacing={0.5}>
        <Typography variant="h4">Catálogo de Proveedores</Typography>
        <Typography variant="body2" color="text.secondary">
          Proveedores vinculados a personas jurídicas o naturales.
        </Typography>
      </Stack>

      <Button
        component={RouterLink}
        to="/compras/proveedores/nuevo"
        variant="contained"
        sx={{ alignSelf: 'flex-start' }}
      >
        Nuevo Proveedor
      </Button>

      {error && <Alert severity="error">{error}</Alert>}

      <Card>
        <CardContent>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>RUC / DNI</TableCell>
                <TableCell>Razón Social</TableCell>
                <TableCell>Teléfono</TableCell>
                <TableCell>Estado</TableCell>
                <TableCell align="right">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {!isLoading &&
                rows.map((row) => (
                  <TableRow key={row.id} hover>
                    <TableCell>{docLabel(row)}</TableCell>
                    <TableCell>{row.razonSocialNombre ?? '-'}</TableCell>
                    <TableCell>{row.telefono ?? '-'}</TableCell>
                    <TableCell>
                      <Chip
                        size="small"
                        label={row.activo ? 'Activo' : 'Inactivo'}
                        color={row.activo ? 'success' : 'default'}
                      />
                    </TableCell>
                    <TableCell align="right">
                      {row.activo && (
                        <Tooltip title="Editar">
                          <IconButton
                            size="small"
                            component={RouterLink}
                            to={`/compras/proveedores/${row.id}/editar`}
                            color="primary"
                          >
                            <EditOutlinedIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}

                      {row.activo ? (
                        <Tooltip title="Desactivar">
                          <IconButton size="small" color="error" onClick={() => handleToggleEstado(row.id, row.activo)}>
                            <DeleteOutlineIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      ) : (
                        <Tooltip title="Activar">
                          <IconButton size="small" color="success" onClick={() => handleToggleEstado(row.id, row.activo)}>
                            <CheckCircleOutlineIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
            </TableBody>
          </Table>
          {!isLoading && rows.length === 0 && (
            <Typography variant="body2" color="text.secondary" sx={{ py: 2 }}>
              No hay proveedores registrados.
            </Typography>
          )}
          {isLoading && (
            <Typography variant="body2" sx={{ py: 2 }}>
              Cargando…
            </Typography>
          )}
        </CardContent>
      </Card>
    </Stack>
  );
}
