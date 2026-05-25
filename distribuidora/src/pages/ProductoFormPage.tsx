import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Button,
  Card,
  CardContent,
  Divider,
  List,
  ListItem,
  ListItemText,
  MenuItem,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import {
  createProducto,
  getProductoById,
  listClasesProducto,
  listIgvCatalogo,
  listUnidadesMedida,
  updateProducto
} from '../services/productoService';
import { extractValidationErrors } from '../services/validation';
import type { CatalogoOption, IgvCatalogoItem } from '../types/producto';

interface FilaPresentacionLocal {
  id: string;
  unidadId: number;
  unidadNombre: string;
  precioCompra: number;
  precioVenta: number;
}

export function ProductoFormPage() {
  const { id } = useParams<{ id: string }>();
  const isEdit = useMemo(() => Boolean(id), [id]);
  const navigate = useNavigate();
  const { session } = useAuth();

  const [codigo, setCodigo] = useState('');
  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [claseProductoId, setClaseProductoId] = useState<number | ''>('');
  const [unidadMedidaBaseId, setUnidadMedidaBaseId] = useState<number | ''>('');
  const [tipoIgvId, setTipoIgvId] = useState<number | ''>('');
  const [precioVentaCatalogo, setPrecioVentaCatalogo] = useState('');
  const [clases, setClases] = useState<CatalogoOption[]>([]);
  const [unidades, setUnidades] = useState<CatalogoOption[]>([]);
  const [tiposIgv, setTiposIgv] = useState<IgvCatalogoItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  // Maquetación: presentaciones / precios (solo UI local)
  const [presUnidadId, setPresUnidadId] = useState<number | ''>('');
  const [precioCompra, setPrecioCompra] = useState('');
  const [precioVenta, setPrecioVenta] = useState('');
  const [filasPresentacion, setFilasPresentacion] = useState<FilaPresentacionLocal[]>([]);

  const añadirPresentacionLocal = useCallback(() => {
    if (presUnidadId === '' || !precioCompra.trim() || !precioVenta.trim()) {
      return;
    }
    const u = unidades.find((x) => x.id === presUnidadId);
    if (!u) {
      return;
    }
    const row: FilaPresentacionLocal = {
      id: `${Date.now()}-${Math.random()}`,
      unidadId: presUnidadId,
      unidadNombre: u.nombre,
      precioCompra: Number(precioCompra),
      precioVenta: Number(precioVenta)
    };
    setFilasPresentacion((prev) => [...prev, row]);
    setPresUnidadId('');
    setPrecioCompra('');
    setPrecioVenta('');
  }, [presUnidadId, precioCompra, precioVenta, unidades]);

  useEffect(() => {
    async function loadData() {
      if (!session?.empresaId) {
        setError('No se pudo determinar la empresa de la sesión.');
        setIsLoading(false);
        return;
      }
      setIsLoading(true);
      setError('');
      try {
        const [clasesData, unidadesData, igvData] = await Promise.all([
          listClasesProducto(session.empresaId),
          listUnidadesMedida(),
          listIgvCatalogo()
        ]);
        setClases(clasesData);
        setUnidades(unidadesData);
        setTiposIgv(igvData);

        if (isEdit && id) {
          const producto = await getProductoById(Number(id));
          setCodigo(producto.codigo);
          setNombre(producto.nombre);
          setDescripcion(producto.descripcion ?? '');
          setClaseProductoId(producto.claseProductoId);
          setUnidadMedidaBaseId(producto.unidadMedidaBaseId);
          if (producto.tipoIgvId != null) {
            setTipoIgvId(producto.tipoIgvId);
          }
          if (producto.precioVenta != null) {
            setPrecioVentaCatalogo(String(producto.precioVenta));
          }
        }
      } catch {
        setError('No se pudo cargar la información del formulario.');
      } finally {
        setIsLoading(false);
      }
    }

    void loadData();
  }, [id, isEdit, session?.empresaId]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');
    setFieldErrors({});

    if (!session?.empresaId) {
      setError('No se pudo determinar la empresa de la sesión.');
      return;
    }
    if (!claseProductoId || !unidadMedidaBaseId || !tipoIgvId) {
      setError('Debes completar clase de producto, unidad base y tipo de afectación IGV.');
      return;
    }

    setIsSubmitting(true);
    try {
      if (isEdit && id) {
        await updateProducto(Number(id), {
          nombre,
          descripcion,
          claseProductoId: Number(claseProductoId),
          unidadMedidaBaseId: Number(unidadMedidaBaseId),
          tipoIgvId: Number(tipoIgvId),
          precioVenta: precioVentaCatalogo.trim() ? Number(precioVentaCatalogo) : 0
        });
      } else {
        await createProducto({
          empresaId: session.empresaId,
          codigo,
          nombre,
          descripcion,
          claseProductoId: Number(claseProductoId),
          unidadMedidaBaseId: Number(unidadMedidaBaseId),
          tipoIgvId: Number(tipoIgvId),
          precioVenta: precioVentaCatalogo.trim() ? Number(precioVentaCatalogo) : 0
        });
      }
      navigate('/inventario/productos', { replace: true });
    } catch (err) {
      const mapped = extractValidationErrors(err);
      if (Object.keys(mapped).length > 0) {
        setFieldErrors(mapped);
      } else {
        setError(isEdit ? 'No se pudo actualizar el producto.' : 'No se pudo crear el producto.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return <Typography>Cargando...</Typography>;
  }

  return (
    <Stack spacing={2.5}>
      <Card>
        <CardContent sx={{ p: 3 }}>
          <Stack spacing={2.5} component="form" onSubmit={handleSubmit}>
            <Stack spacing={0.5}>
              <Typography variant="h5">{isEdit ? 'Editar Producto' : 'Nuevo Producto'}</Typography>
              <Typography variant="body2" color="text.secondary">
                Datos del catálogo y afectación tributaria.
              </Typography>
            </Stack>

            {error && <Alert severity="error">{error}</Alert>}

            <TextField
              label="Código"
              value={codigo}
              onChange={(e) => setCodigo(e.target.value)}
              disabled={isEdit}
              error={Boolean(fieldErrors.codigo)}
              helperText={isEdit ? 'El código no se edita.' : fieldErrors.codigo}
              required
            />
            <TextField
              label="Nombre"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              error={Boolean(fieldErrors.nombre)}
              helperText={fieldErrors.nombre}
              required
            />
            <TextField
              label="Descripción"
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              multiline
              minRows={2}
            />
            <TextField
              select
              label="Clase de Producto"
              value={claseProductoId}
              onChange={(e) => setClaseProductoId(Number(e.target.value))}
              error={Boolean(fieldErrors.claseProductoId)}
              helperText={fieldErrors.claseProductoId}
              required
            >
              {clases.map((item) => (
                <MenuItem key={item.id} value={item.id}>
                  {item.nombre}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Unidad de Medida Base"
              value={unidadMedidaBaseId}
              onChange={(e) => setUnidadMedidaBaseId(Number(e.target.value))}
              error={Boolean(fieldErrors.unidadMedidaBaseId)}
              helperText={fieldErrors.unidadMedidaBaseId}
              required
            >
              {unidades.map((item) => (
                <MenuItem key={item.id} value={item.id}>
                  {item.nombre}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Tipo de Afectación IGV"
              value={tipoIgvId}
              onChange={(e) => setTipoIgvId(Number(e.target.value))}
              error={Boolean(fieldErrors.tipoIgvId)}
              helperText={fieldErrors.tipoIgvId}
              required
            >
              {tiposIgv.map((igv) => (
                <MenuItem key={igv.id} value={igv.id}>
                  {igv.nombre} ({igv.porcentaje}%)
                </MenuItem>
              ))}
            </TextField>
            <TextField
              label="Precio de venta (catálogo)"
              type="number"
              value={precioVentaCatalogo}
              onChange={(e) => setPrecioVentaCatalogo(e.target.value)}
              error={Boolean(fieldErrors.precioVenta)}
              helperText={fieldErrors.precioVenta ?? 'Usado en el punto de venta al agregar el producto.'}
              inputProps={{ min: 0, step: 'any' }}
            />

            <Button type="submit" variant="contained" disabled={isSubmitting}>
              {isSubmitting ? 'Guardando...' : isEdit ? 'Guardar Cambios' : 'Crear Producto'}
            </Button>
          </Stack>
        </CardContent>
      </Card>

      <Card variant="outlined">
        <CardContent sx={{ p: 3 }}>
          <Stack spacing={2}>
            <Stack spacing={0.5}>
              <Typography variant="h6">Presentaciones y Precios</Typography>
              <Typography variant="body2" color="text.secondary">
                Próximamente podrás vincular presentaciones y listas de precios. Aquí solo maquetamos la
                acción de añadir.
              </Typography>
            </Stack>
            <Divider />
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems={{ sm: 'flex-start' }} flexWrap="wrap">
              <TextField
                select
                label="Presentación / Unidad"
                value={presUnidadId}
                onChange={(e) => setPresUnidadId(e.target.value === '' ? '' : Number(e.target.value))}
                sx={{ minWidth: 220 }}
              >
                <MenuItem value="">
                  <em>Seleccionar</em>
                </MenuItem>
                {unidades.map((u) => (
                  <MenuItem key={u.id} value={u.id}>
                    {u.nombre}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                label="Precio de Compra"
                type="number"
                inputProps={{ min: 0, step: 0.01 }}
                value={precioCompra}
                onChange={(e) => setPrecioCompra(e.target.value)}
                sx={{ minWidth: 160 }}
              />
              <TextField
                label="Precio de Venta"
                type="number"
                inputProps={{ min: 0, step: 0.01 }}
                value={precioVenta}
                onChange={(e) => setPrecioVenta(e.target.value)}
                sx={{ minWidth: 160 }}
              />
              <Button variant="outlined" onClick={añadirPresentacionLocal} sx={{ mt: { xs: 0, sm: 1 } }}>
                Añadir
              </Button>
            </Stack>
            {filasPresentacion.length > 0 && (
              <List dense>
                {filasPresentacion.map((f) => (
                  <ListItem key={f.id} disableGutters>
                    <ListItemText
                      primary={`${f.unidadNombre}`}
                      secondary={`Compra: S/ ${f.precioCompra.toFixed(2)} · Venta: S/ ${f.precioVenta.toFixed(2)}`}
                    />
                  </ListItem>
                ))}
              </List>
            )}
          </Stack>
        </CardContent>
      </Card>
    </Stack>
  );
}
