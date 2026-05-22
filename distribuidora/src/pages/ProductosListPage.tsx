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
import { listProductos, toggleEstadoProducto } from '../services/productoService';
import type { ProductoResponse } from '../types/producto';

export function ProductosListPage() {
  const { session } = useAuth();
  const [rows, setRows] = useState<ProductoResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  async function loadProductos() {
    if (!session?.empresaId) {
      setError('No se pudo determinar la empresa de la sesión.');
      setIsLoading(false);
      return;
    }
    setIsLoading(true);
    setError('');
    try {
      const data = await listProductos(session.empresaId, 0, 100);
      setRows(data.content);
    } catch {
      setError('No se pudo cargar la lista de productos.');
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadProductos();
  }, [session?.empresaId]);

  const handleToggleEstado = async (id: number, activo: boolean) => {
    try {
      await toggleEstadoProducto(id);
      await loadProductos();
    } catch {
      setError(activo ? 'No se pudo desactivar el producto.' : 'No se pudo activar el producto.');
    }
  };

  return (
    <Stack spacing={2}>
      <Stack spacing={0.5}>
        <Typography variant="h4">Catálogo de Productos</Typography>
        <Typography variant="body2" color="text.secondary">
          Gestión de productos del módulo de inventario.
        </Typography>
      </Stack>

      <Button component={RouterLink} to="/inventario/productos/nuevo" variant="contained" sx={{ alignSelf: 'flex-start' }}>
        Nuevo Producto
      </Button>

      {error && <Alert severity="error">{error}</Alert>}

      <Card>
        <CardContent>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Código</TableCell>
                <TableCell>Nombre</TableCell>
                <TableCell>Clase</TableCell>
                <TableCell>Unidad Base</TableCell>
                <TableCell>IGV</TableCell>
                <TableCell>Estado</TableCell>
                <TableCell align="right">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map((row) => (
                <TableRow key={row.id} hover>
                  <TableCell>{row.codigo}</TableCell>
                  <TableCell>{row.nombre}</TableCell>
                  <TableCell>{row.claseProductoNombre ?? '-'}</TableCell>
                  <TableCell>{row.unidadMedidaNombre ?? '-'}</TableCell>
                  <TableCell>
                    {row.tipoIgvNombre != null
                      ? `${row.tipoIgvNombre} (${row.tipoIgvPorcentaje != null ? row.tipoIgvPorcentaje : '-'}%)`
                      : '-'}
                  </TableCell>
                  <TableCell>
                    <Chip size="small" label={row.activo ? 'Activo' : 'Inactivo'} color={row.activo ? 'success' : 'default'} />
                  </TableCell>
                  <TableCell align="right">
                    {row.activo && (
                      <Tooltip title="Editar">
                        <IconButton component={RouterLink} to={`/inventario/productos/${row.id}/editar`} size="small" color="primary">
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
              {!rows.length && !isLoading && (
                <TableRow>
                  <TableCell colSpan={7}>
                    <Typography variant="body2" color="text.secondary">
                      No hay productos registrados.
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
