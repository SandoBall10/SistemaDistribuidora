import AddCircleOutlineRoundedIcon from '@mui/icons-material/AddCircleOutlineRounded';
import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded';
import SaveRoundedIcon from '@mui/icons-material/SaveRounded';
import {
  Alert,
  Autocomplete,
  Box,
  Button,
  Card,
  CardContent,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  MenuItem,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography
} from '@mui/material';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { isAxiosError } from 'axios';
import { createCompra } from '../services/compraService';
import { listProductos } from '../services/productoService';
import { listProveedores } from '../services/proveedorService';
import { extractValidationErrors } from '../services/validation';
import { useAuth } from '../hooks/useAuth';
import type { CompraDetalleCreatePayload } from '../types/compra';
import type { ProductoResponse } from '../types/producto';
import type { ProveedorResponse } from '../types/proveedor';

type DetalleUi = CompraDetalleCreatePayload & {
  key: string;
  codigoProducto: string;
  nombreProducto: string;
};

const TIPO_COMP = [
  { value: 'FACTURA', label: 'Factura' },
  { value: 'BOLETA', label: 'Boleta' },
  { value: 'GUIA', label: 'Guía de ingreso' }
];

const MONEDAS = [
  { value: 'PEN', label: 'Soles (PEN)' },
  { value: 'USD', label: 'Dólares (USD)' }
];

async function cargarTodosProveedores(empresaId: number): Promise<ProveedorResponse[]> {
  const pageSize = 100;
  let page = 0;
  const out: ProveedorResponse[] = [];
  for (;;) {
    const res = await listProveedores(empresaId, page, pageSize);
    out.push(...res.content);
    if (page >= res.totalPages - 1) break;
    page++;
  }
  return out;
}

async function cargarTodosProductos(empresaId: number): Promise<ProductoResponse[]> {
  const pageSize = 100;
  let page = 0;
  const out: ProductoResponse[] = [];
  for (;;) {
    const res = await listProductos(empresaId, page, pageSize);
    out.push(...res.content);
    if (page >= res.totalPages - 1) break;
    page++;
  }
  return out.filter((p) => p.activo);
}

function labelProveedor(p: ProveedorResponse) {
  return (
    p.razonSocialNombre?.trim() ||
    p.razonSocial?.trim() ||
    [p.nombres, p.apellidoPaterno, p.apellidoMaterno].filter(Boolean).join(' ') ||
    p.codigoProveedor
  );
}

function fmtMoney(n: number, moneda: string) {
  const cur = moneda === 'USD' ? 'USD' : 'PEN';
  return new Intl.NumberFormat('es-PE', { style: 'currency', currency: cur }).format(n);
}

export function CompraFormPage() {
  const { session } = useAuth();
  const navigate = useNavigate();
  const empresaId = session?.empresaId;

  const [proveedores, setProveedores] = useState<ProveedorResponse[]>([]);
  const [productos, setProductos] = useState<ProductoResponse[]>([]);
  const [catalogLoading, setCatalogLoading] = useState(true);

  const [proveedor, setProveedor] = useState<ProveedorResponse | null>(null);
  const [fechaIngreso, setFechaIngreso] = useState(() => new Date().toISOString().slice(0, 10));
  const [almacen, setAlmacen] = useState('');
  const [tipoComprobante, setTipoComprobante] = useState('FACTURA');
  const [numeroComprobante, setNumeroComprobante] = useState('');
  const [moneda, setMoneda] = useState('PEN');

  const [filas, setFilas] = useState<DetalleUi[]>([]);

  const [modalOpen, setModalOpen] = useState(false);
  const [prodSel, setProdSel] = useState<ProductoResponse | null>(null);
  const [cantidadMod, setCantidadMod] = useState<number | ''>(1);
  const [precioMod, setPrecioMod] = useState<number | ''>(0);
  const [loteMod, setLoteMod] = useState('');
  const [fProdMod, setFProdMod] = useState('');
  const [fVenMod, setFVenMod] = useState('');
  const [modalErrors, setModalErrors] = useState('');

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (!empresaId) return;
    let cancelled = false;
    (async () => {
      try {
        setCatalogLoading(true);
        const [provs, prods] = await Promise.all([cargarTodosProveedores(empresaId), cargarTodosProductos(empresaId)]);
        if (!cancelled) {
          setProveedores(provs.filter((x) => x.activo));
          setProductos(prods);
        }
      } catch {
        if (!cancelled) setError('No se pudieron cargar proveedores o productos.');
      } finally {
        if (!cancelled) setCatalogLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [empresaId]);

  const totalDocumento = useMemo(
    () => filas.reduce((acc, row) => acc + Number(row.cantidad) * Number(row.precioUnitario), 0),
    [filas]
  );

  const abrirModal = useCallback(() => {
    setModalErrors('');
    setProdSel(null);
    setCantidadMod(1);
    setPrecioMod(0);
    setLoteMod('');
    setFProdMod('');
    setFVenMod('');
    setModalOpen(true);
  }, []);

  const cerrarModal = useCallback(() => setModalOpen(false), []);

  const confirmarModal = useCallback(() => {
    setModalErrors('');
    if (!prodSel) {
      setModalErrors('Seleccione un producto.');
      return;
    }
    const c = typeof cantidadMod === 'number' ? cantidadMod : Number.NaN;
    const p = typeof precioMod === 'number' ? precioMod : Number.NaN;
    if (!(c > 0)) {
      setModalErrors('La cantidad debe ser mayor a cero.');
      return;
    }
    if (!(p >= 0) || Number.isNaN(p)) {
      setModalErrors('Precio de compra no válido.');
      return;
    }
    const lote = loteMod.trim();
    if (!lote) {
      setModalErrors('Indique el código de lote.');
      return;
    }
    const key = crypto.randomUUID();
    const fila: DetalleUi = {
      key,
      productoId: prodSel.id,
      codigoProducto: prodSel.codigo,
      nombreProducto: prodSel.nombre,
      cantidad: c,
      precioUnitario: p,
      loteCodigo: lote,
      fechaProduccion: fProdMod || null,
      fechaVencimiento: fVenMod || null
    };
    setFilas((prev) => [...prev, fila]);
    cerrarModal();
  }, [prodSel, cantidadMod, precioMod, loteMod, fProdMod, fVenMod, cerrarModal]);

  const eliminarFila = useCallback((key: string) => {
    setFilas((prev) => prev.filter((r) => r.key !== key));
  }, []);

  const guardarCompra = useCallback(async () => {
    setError('');
    setFieldErrors({});
    setSuccess('');
    if (!empresaId) return;
    if (!proveedor) {
      setError('Seleccione un proveedor.');
      return;
    }
    if (!fechaIngreso) {
      setError('La fecha de ingreso es obligatoria.');
      return;
    }
    if (!numeroComprobante.trim()) {
      setError('El número de comprobante es obligatorio.');
      return;
    }
    if (filas.length === 0) {
      setError('Agregue al menos un producto al detalle.');
      return;
    }

    const detallesPayload: CompraDetalleCreatePayload[] = filas.map(
      ({ key: _k, codigoProducto: _c, nombreProducto: _n, ...rest }) => ({
        ...rest,
        fechaProduccion: rest.fechaProduccion || undefined,
        fechaVencimiento: rest.fechaVencimiento || undefined
      })
    );

    try {
      setSubmitting(true);
      const res = await createCompra({
        proveedorId: proveedor.id,
        fechaIngreso,
        almacen: almacen.trim() || undefined,
        tipoComprobanteCodigo: tipoComprobante,
        numeroComprobante: numeroComprobante.trim(),
        monedaCodigo: moneda,
        detalles: detallesPayload
      });
      setSuccess(`Compra registrada correctamente (#${res.id}). El stock por lote fue actualizado.`);
      setProveedor(null);
      setFilas([]);
      setNumeroComprobante('');
      setAlmacen('');
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (err) {
      const ve = extractValidationErrors(err);
      if (Object.keys(ve).length) {
        setFieldErrors(ve);
      }
      if (isAxiosError(err)) {
        const msg = (err.response?.data as { message?: string } | undefined)?.message;
        setError(msg ?? 'No se pudo registrar la compra.');
      } else {
        setError('No se pudo registrar la compra.');
      }
    } finally {
      setSubmitting(false);
    }
  }, [empresaId, proveedor, fechaIngreso, numeroComprobante, almacen, tipoComprobante, moneda, filas]);

  if (!empresaId) {
    return (
      <Alert severity="warning" sx={{ m: 2 }}>
        Sesión sin empresa asignada. Vuelva a iniciar sesión.
      </Alert>
    );
  }

  return (
    <Box sx={{ pb: 4 }}>
      <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" alignItems={{ md: 'center' }} spacing={2} sx={{ mb: 3 }}>
        <Box>
          <Typography variant="h5" component="h1" fontWeight={700}>
            Ingreso de productos (compra)
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Registro tipo maestro‑detalle: cabecera, líneas de producto por lote y actualización automática de stock en almacenes.
          </Typography>
        </Box>
        <Stack direction="row" spacing={1}>
          <Button variant="outlined" onClick={() => navigate('/compras/proveedores')} size="medium">
            Ir a proveedores
          </Button>
          <Button
            variant="contained"
            startIcon={<SaveRoundedIcon />}
            onClick={() => void guardarCompra()}
            disabled={submitting || catalogLoading}
          >
            Guardar compra
          </Button>
        </Stack>
      </Stack>

      {success ? (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>
          {success}
        </Alert>
      ) : null}
      {error ? (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
          {error}
        </Alert>
      ) : null}

      <Stack spacing={2.5}>
        <Card variant="outlined">
          <CardContent>
            <Typography variant="subtitle1" fontWeight={600} sx={{ mb: 2 }}>
              Cabecera del documento
            </Typography>
            <Stack spacing={2}>
              <Autocomplete
                disabled={catalogLoading || submitting}
                options={proveedores}
                getOptionLabel={(o) => labelProveedor(o)}
                loading={catalogLoading}
                value={proveedor}
                onChange={(_e, v) => setProveedor(v)}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Proveedor"
                    required
                    error={Boolean(fieldErrors.proveedorId)}
                    helperText={fieldErrors.proveedorId}
                  />
                )}
              />
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                <TextField
                  label="Fecha de ingreso"
                  type="date"
                  value={fechaIngreso}
                  onChange={(e) => setFechaIngreso(e.target.value)}
                  InputLabelProps={{ shrink: true }}
                  sx={{ flex: 1 }}
                  required
                  error={Boolean(fieldErrors.fechaIngreso)}
                  helperText={fieldErrors.fechaIngreso}
                />
                <TextField
                  label="Almacén / ubicación destino"
                  value={almacen}
                  onChange={(e) => setAlmacen(e.target.value)}
                  placeholder="Ej.: Almacén central — Andén A"
                  sx={{ flex: 2 }}
                  error={Boolean(fieldErrors.almacen)}
                  helperText={fieldErrors.almacen}
                />
              </Stack>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                <TextField
                  select
                  label="Tipo de comprobante"
                  value={tipoComprobante}
                  onChange={(e) => setTipoComprobante(e.target.value)}
                  sx={{ flex: 1 }}
                  required
                >
                  {TIPO_COMP.map((t) => (
                    <MenuItem key={t.value} value={t.value}>
                      {t.label}
                    </MenuItem>
                  ))}
                </TextField>
                <TextField
                  label="Número de comprobante"
                  value={numeroComprobante}
                  onChange={(e) => setNumeroComprobante(e.target.value)}
                  required
                  sx={{ flex: 1 }}
                  error={Boolean(fieldErrors.numeroComprobante)}
                  helperText={fieldErrors.numeroComprobante}
                />
                <TextField
                  select
                  label="Moneda"
                  value={moneda}
                  onChange={(e) => setMoneda(e.target.value)}
                  sx={{ flex: 1 }}
                  required
                >
                  {MONEDAS.map((m) => (
                    <MenuItem key={m.value} value={m.value}>
                      {m.label}
                    </MenuItem>
                  ))}
                </TextField>
              </Stack>
            </Stack>
          </CardContent>
        </Card>

        <Card variant="outlined">
          <CardContent>
            <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" alignItems={{ md: 'center' }} spacing={2} sx={{ mb: 2 }}>
              <Typography variant="subtitle1" fontWeight={600}>
                Detalle de productos ({filas.length} líneas)
              </Typography>
              <Button
                variant="outlined"
                startIcon={<AddCircleOutlineRoundedIcon />}
                onClick={abrirModal}
                disabled={catalogLoading || submitting}
              >
                Agregar producto
              </Button>
            </Stack>

            <TableContainer sx={{ border: 1, borderColor: 'divider', borderRadius: 2, maxHeight: 440 }}>
              <Table size="small" stickyHeader>
                <TableHead>
                  <TableRow>
                    <TableCell>Código</TableCell>
                    <TableCell>Producto</TableCell>
                    <TableCell align="right">Cantidad</TableCell>
                    <TableCell align="right">P. compra</TableCell>
                    <TableCell align="right">Subtotal</TableCell>
                    <TableCell>Lote</TableCell>
                    <TableCell>Prod.</TableCell>
                    <TableCell>Venc.</TableCell>
                    <TableCell width={56} />
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filas.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={9}>
                        <Typography variant="body2" color="text.secondary" sx={{ py: 3 }} align="center">
                          No hay líneas.&nbsp;Pulse «Agregar producto» para registrar ingresos por lote.
                        </Typography>
                      </TableCell>
                    </TableRow>
                  ) : (
                    filas.map((row) => {
                      const sub = Number(row.cantidad) * Number(row.precioUnitario);
                      return (
                        <TableRow hover key={row.key}>
                          <TableCell sx={{ whiteSpace: 'nowrap' }}>{row.codigoProducto}</TableCell>
                          <TableCell>{row.nombreProducto}</TableCell>
                          <TableCell align="right">{row.cantidad}</TableCell>
                          <TableCell align="right">{fmtMoney(Number(row.precioUnitario), moneda)}</TableCell>
                          <TableCell align="right">{fmtMoney(sub, moneda)}</TableCell>
                          <TableCell>{row.loteCodigo}</TableCell>
                          <TableCell>{row.fechaProduccion || '—'}</TableCell>
                          <TableCell>{row.fechaVencimiento || '—'}</TableCell>
                          <TableCell align="center">
                            <IconButton aria-label="Quitar línea" size="small" onClick={() => eliminarFila(row.key)} disabled={submitting}>
                              <DeleteOutlineRoundedIcon fontSize="small" />
                            </IconButton>
                          </TableCell>
                        </TableRow>
                      );
                    })
                  )}
                </TableBody>
              </Table>
            </TableContainer>

            <Stack direction="row" justifyContent="flex-end" sx={{ mt: 2 }}>
              <Typography variant="subtitle1" fontWeight={700}>
                Total documento:&nbsp;
                {fmtMoney(totalDocumento, moneda)}
              </Typography>
            </Stack>
          </CardContent>
        </Card>

        <Typography variant="caption" color="text.secondary">
          Al guardar, el sistema creará la compra en base de datos, las líneas y actualizará o creará el lote físico incrementando&nbsp;
          <code>stock_actual</code> por producto y código de lote.
        </Typography>
      </Stack>

      <Dialog open={modalOpen} onClose={cerrarModal} maxWidth="md" fullWidth>
        <DialogTitle>Agregar línea — producto y lote</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1 }}>
            {modalErrors ? <Alert severity="warning">{modalErrors}</Alert> : null}
            <Autocomplete
              disabled={catalogLoading}
              options={productos}
              loading={catalogLoading}
              value={prodSel}
              onChange={(_e, v) => setProdSel(v)}
              getOptionLabel={(o) => `${o.codigo} — ${o.nombre}`}
              renderInput={(params) => <TextField {...params} label="Producto" required />}
            />
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField
                label="Cantidad"
                type="number"
                value={cantidadMod}
                onChange={(e) => {
                  const v = e.target.value;
                  setCantidadMod(v === '' ? '' : Number(v));
                }}
                inputProps={{ min: 0.0001, step: 'any' }}
                sx={{ flex: 1 }}
                required
              />
              <TextField
                label="Precio de compra unitario"
                type="number"
                value={precioMod}
                onChange={(e) => {
                  const v = e.target.value;
                  setPrecioMod(v === '' ? '' : Number(v));
                }}
                inputProps={{ min: 0, step: 'any' }}
                sx={{ flex: 1 }}
                required
              />
            </Stack>
            <TextField label="Lote" value={loteMod} onChange={(e) => setLoteMod(e.target.value)} required placeholder="Código físico del lote" />
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField
                label="Fecha producción"
                type="date"
                value={fProdMod}
                onChange={(e) => setFProdMod(e.target.value)}
                InputLabelProps={{ shrink: true }}
                sx={{ flex: 1 }}
              />
              <TextField
                label="Fecha vencimiento"
                type="date"
                value={fVenMod}
                onChange={(e) => setFVenMod(e.target.value)}
                InputLabelProps={{ shrink: true }}
                sx={{ flex: 1 }}
              />
            </Stack>
          </Stack>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={cerrarModal}>Cancelar</Button>
          <Button variant="contained" onClick={confirmarModal}>
            Añadir a la tabla
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
