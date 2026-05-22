import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded';
import {
  Alert,
  Box,
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
import { useCallback, useEffect, useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { listClientesActivos } from '../services/clienteService';
import type { ClienteResponse } from '../types/cliente';

function labelCliente(c: ClienteResponse) {
  return c.razonSocialNombre?.trim() || c.codigoCliente;
}

export function ClientesListPage() {
  const [rows, setRows] = useState<ClienteResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await listClientesActivos();
      setRows(data);
    } catch {
      setError('No se pudo cargar el listado de clientes.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  return (
    <Stack spacing={2}>
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} spacing={1}>
        <Box>
          <Typography variant="h4" fontWeight={700}>
            Clientes
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Personas registradas como clientes para facturación y punto de venta.
          </Typography>
        </Box>
        <Stack direction="row" spacing={1}>
          <Tooltip title="Actualizar">
            <IconButton onClick={() => void load()} disabled={loading}>
              <RefreshRoundedIcon />
            </IconButton>
          </Tooltip>
          <Button component={RouterLink} to="/ventas" variant="contained">
            Ir a punto de venta
          </Button>
        </Stack>
      </Stack>

      {error ? <Alert severity="error">{error}</Alert> : null}

      <Card>
        <CardContent>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Código</TableCell>
                <TableCell>Documento</TableCell>
                <TableCell>Nombre / Razón social</TableCell>
                <TableCell>Dirección</TableCell>
                <TableCell>Estado</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={5}>
                    <Typography color="text.secondary" align="center" py={3}>
                      Cargando…
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : rows.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5}>
                    <Typography color="text.secondary" align="center" py={3}>
                      No hay clientes activos. Regístrelos desde el punto de venta con el botón [ + ].
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : (
                rows.map((row) => (
                  <TableRow key={row.id} hover>
                    <TableCell>{row.codigoCliente}</TableCell>
                    <TableCell sx={{ whiteSpace: 'nowrap' }}>{row.numeroDocumento ?? '—'}</TableCell>
                    <TableCell>{labelCliente(row)}</TableCell>
                    <TableCell>{row.direccion?.trim() || '—'}</TableCell>
                    <TableCell>
                      <Chip size="small" label={row.activo ? 'Activo' : 'Inactivo'} color={row.activo ? 'success' : 'default'} />
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </Stack>
  );
}
