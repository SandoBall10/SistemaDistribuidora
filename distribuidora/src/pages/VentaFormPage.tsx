import AddRoundedIcon from '@mui/icons-material/AddRounded';
import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded';
import PointOfSaleRoundedIcon from '@mui/icons-material/PointOfSaleRounded';
import {
  Alert,
  Autocomplete,
  Box,
  Button,
  Card,
  CardContent,
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
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { isAxiosError } from 'axios';
import { listClientesActivos } from '../services/clienteService';
import { listLotesDisponibles } from '../services/loteService';
import { listProductos } from '../services/productoService';
import { createVenta } from '../services/ventaService';
import { extractValidationErrors } from '../services/validation';
import { ClienteQuickCreateDialog } from '../components/ventas/ClienteQuickCreateDialog';
import { TicketImpresionDialog } from '../components/ventas/TicketImpresionDialog';
import { useAuth } from '../hooks/useAuth';
import type { ClienteResponse } from '../types/cliente';
import type { LoteDisponible } from '../types/lote';
import type { ProductoResponse } from '../types/producto';

type CartRow = {
  key: string;
  productoId: number;
  codigoProducto: string;
  nombreProducto: string;
  loteCodigo: string;
  lotesOpciones: LoteDisponible[];
  lotesLoading: boolean;
  cantidad: number;
  precioUnitario: number;
};

const IGV_FACTOR = 1.18;

const TIPOS_COMP = [
  { value: 'FACTURA', label: 'Factura', serie: 'F001' },
  { value: 'BOLETA', label: 'Boleta', serie: 'B001' }
];

const MONEDAS = [
  { value: 'PEN', label: 'Soles (PEN)' },
  { value: 'USD', label: 'Dólares (USD)' }
];

function seriePorTipo(tipo: string) {
  return TIPOS_COMP.find((t) => t.value === tipo)?.serie ?? 'F001';
}

function labelCliente(c: ClienteResponse) {
  const nombre = c.razonSocialNombre?.trim() || c.codigoCliente;
  const doc = c.numeroDocumento ? ` — ${c.numeroDocumento}` : '';
  return `${nombre}${doc}`;
}

function fmtMoney(n: number, moneda: string) {
  const cur = moneda === 'USD' ? 'USD' : 'PEN';
  return new Intl.NumberFormat('es-PE', { style: 'currency', currency: cur }).format(n);
}

function nextNumeroComprobante() {
  return String(Date.now() % 100_000_000).padStart(8, '0');
}

function labelLoteOpcion(lote: LoteDisponible) {
  const stock = Number(lote.stockActual);
  const stockFmt = Number.isInteger(stock) ? String(stock) : stock.toFixed(2);
  return `${lote.codigoLote} (Stock: ${stockFmt})`;
}

function precioCatalogo(prod: ProductoResponse) {
  const p = prod.precioVenta;
  return p != null && !Number.isNaN(Number(p)) ? Number(p) : 0;
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

export function VentaFormPage() {
  const { session } = useAuth();
  const empresaId = session?.empresaId;
  const productSearchRef = useRef<HTMLInputElement | null>(null);

  const [clientes, setClientes] = useState<ClienteResponse[]>([]);
  const [productos, setProductos] = useState<ProductoResponse[]>([]);
  const [catalogLoading, setCatalogLoading] = useState(true);

  const [cliente, setCliente] = useState<ClienteResponse | null>(null);
  const [tipoComprobante, setTipoComprobante] = useState('BOLETA');
  const [serie, setSerie] = useState('B001');
  const [numeroComprobante, setNumeroComprobante] = useState(() => nextNumeroComprobante());
  const [fechaEmision, setFechaEmision] = useState(() => new Date().toISOString().slice(0, 10));
  const [moneda, setMoneda] = useState('PEN');

  const [carrito, setCarrito] = useState<CartRow[]>([]);

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [success, setSuccess] = useState('');
  const [clienteModalOpen, setClienteModalOpen] = useState(false);
  const [ticketVentaId, setTicketVentaId] = useState<number | null>(null);

  const onClienteCreado = useCallback((nuevo: ClienteResponse) => {
    setClientes((prev) => {
      const exists = prev.some((c) => c.id === nuevo.id);
      return exists ? prev : [...prev, nuevo];
    });
    setCliente(nuevo);
    setSuccess('');
    setError('');
  }, []);

  useEffect(() => {
    if (!empresaId) return;
    let cancelled = false;
    (async () => {
      try {
        setCatalogLoading(true);
        const [cls, prods] = await Promise.all([listClientesActivos(), cargarTodosProductos(empresaId)]);
        if (!cancelled) {
          setClientes(cls);
          setProductos(prods);
          if (cls.length === 1) setCliente(cls[0]);
        }
      } catch {
        if (!cancelled) setError('No se pudieron cargar clientes o productos.');
      } finally {
        if (!cancelled) setCatalogLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [empresaId]);

  const onTipoChange = useCallback((tipo: string) => {
    setTipoComprobante(tipo);
    setSerie(seriePorTipo(tipo));
  }, []);

  const lineSubtotal = useCallback((row: CartRow) => row.cantidad * row.precioUnitario, []);

  const totales = useMemo(() => {
    const totalVenta = carrito.reduce((acc, row) => acc + lineSubtotal(row), 0);
    const totalGravado = Math.round((totalVenta / IGV_FACTOR) * 100) / 100;
    const totalIgv = Math.round((totalVenta - totalGravado) * 100) / 100;
    return { totalVenta, totalGravado, totalIgv };
  }, [carrito, lineSubtotal]);

  const agregarProducto = useCallback((prod: ProductoResponse | null) => {
    if (!prod) return;
    const key = crypto.randomUUID();
    setCarrito((prev) => [
      ...prev,
      {
        key,
        productoId: prod.id,
        codigoProducto: prod.codigo,
        nombreProducto: prod.nombre,
        loteCodigo: '',
        lotesOpciones: [],
        lotesLoading: true,
        cantidad: 1,
        precioUnitario: precioCatalogo(prod)
      }
    ]);
    setTimeout(() => productSearchRef.current?.focus(), 0);

    void (async () => {
      try {
        const lotes = await listLotesDisponibles(prod.id);
        setCarrito((prev) =>
          prev.map((r) => {
            if (r.key !== key) return r;
            const loteCodigo = lotes.length === 1 ? lotes[0].codigoLote : r.loteCodigo;
            return {
              ...r,
              lotesOpciones: lotes,
              lotesLoading: false,
              loteCodigo
            };
          })
        );
      } catch {
        setCarrito((prev) =>
          prev.map((r) => (r.key === key ? { ...r, lotesLoading: false } : r))
        );
      }
    })();
  }, []);

  const actualizarFila = useCallback((key: string, patch: Partial<CartRow>) => {
    setCarrito((prev) => prev.map((r) => (r.key === key ? { ...r, ...patch } : r)));
  }, []);

  const eliminarFila = useCallback((key: string) => {
    setCarrito((prev) => prev.filter((r) => r.key !== key));
  }, []);

  const limpiarVenta = useCallback(() => {
    setCarrito([]);
    setNumeroComprobante(nextNumeroComprobante());
    setSuccess('');
    setError('');
    setFieldErrors({});
    setTimeout(() => productSearchRef.current?.focus(), 0);
  }, []);

  const emitirComprobante = useCallback(async () => {
    setError('');
    setFieldErrors({});
    setSuccess('');

    if (!cliente) {
      setError('Seleccione un cliente.');
      return;
    }
    if (carrito.length === 0) {
      setError('Agregue al menos un producto al carrito.');
      return;
    }
    const sinLote = carrito.find((r) => !r.loteCodigo.trim());
    if (sinLote) {
      setError(`Indique el lote del producto ${sinLote.codigoProducto}.`);
      return;
    }
    const sinPrecio = carrito.find((r) => !(r.precioUnitario >= 0) || r.cantidad <= 0);
    if (sinPrecio) {
      setError('Revise cantidad y precio de cada línea.');
      return;
    }

    try {
      setSubmitting(true);
      const res = await createVenta({
        clienteId: cliente.id,
        fechaEmision,
        tipoComprobanteCodigo: tipoComprobante,
        serie: serie.trim(),
        numeroComprobante: numeroComprobante.trim(),
        monedaCodigo: moneda,
        totalGravado: totales.totalGravado,
        totalIgv: totales.totalIgv,
        totalVenta: totales.totalVenta,
        detalles: carrito.map((r) => ({
          productoId: r.productoId,
          cantidad: r.cantidad,
          precioUnitario: r.precioUnitario,
          loteCodigo: r.loteCodigo.trim()
        }))
      });
      setSuccess(
        `Comprobante ${res.serie}-${res.numeroComprobante} emitido (#${res.id}). Stock descontado.`
      );
      setTicketVentaId(res.id);
      limpiarVenta();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (err) {
      const ve = extractValidationErrors(err);
      if (Object.keys(ve).length) setFieldErrors(ve);
      if (isAxiosError(err)) {
        const msg = (err.response?.data as { message?: string } | undefined)?.message;
        setError(msg ?? 'No se pudo registrar la venta.');
      } else {
        setError('No se pudo registrar la venta.');
      }
    } finally {
      setSubmitting(false);
    }
  }, [
    cliente,
    carrito,
    fechaEmision,
    tipoComprobante,
    serie,
    numeroComprobante,
    moneda,
    totales,
    limpiarVenta
  ]);

  if (!empresaId) {
    return (
      <Alert severity="warning" sx={{ m: 2 }}>
        Sesión sin empresa asignada. Vuelva a iniciar sesión.
      </Alert>
    );
  }

  return (
    <Box
      sx={{
        display: 'grid',
        gridTemplateColumns: { xs: '1fr', lg: '1fr minmax(300px, 360px)' },
        gap: 2,
        alignItems: 'start',
        minHeight: 'calc(100vh - 100px)'
      }}
    >
      <Stack spacing={2}>
        {success ? <Alert severity="success">{success}</Alert> : null}
        {error ? <Alert severity="error">{error}</Alert> : null}

        <Card variant="outlined">
          <CardContent sx={{ py: 2, '&:last-child': { pb: 2 } }}>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom>
              Datos del comprobante
            </Typography>
            <Box
              sx={{
                display: 'grid',
                gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: 'repeat(3, 1fr)' },
                gap: 2
              }}
            >
              <Box sx={{ display: 'flex', gap: 0.5, alignItems: 'flex-start', gridColumn: { xs: '1 / -1', md: 'span 2' } }}>
                <Autocomplete
                  disabled={catalogLoading || submitting}
                  options={clientes}
                  value={cliente}
                  onChange={(_e, v) => setCliente(v)}
                  getOptionLabel={labelCliente}
                  sx={{ flex: 1 }}
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      label="Cliente"
                      required
                      error={Boolean(fieldErrors.clienteId)}
                      helperText={fieldErrors.clienteId}
                    />
                  )}
                />
                <IconButton
                  color="primary"
                  onClick={() => setClienteModalOpen(true)}
                  disabled={catalogLoading || submitting}
                  aria-label="Registrar cliente nuevo"
                  sx={{
                    mt: 0.5,
                    border: 1,
                    borderColor: 'primary.main',
                    borderRadius: 2,
                    width: 44,
                    height: 44
                  }}
                >
                  <AddRoundedIcon />
                </IconButton>
              </Box>
              <TextField
                select
                label="Tipo comprobante"
                value={tipoComprobante}
                onChange={(e) => onTipoChange(e.target.value)}
                disabled={submitting}
              >
                {TIPOS_COMP.map((t) => (
                  <MenuItem key={t.value} value={t.value}>
                    {t.label}
                  </MenuItem>
                ))}
              </TextField>
              <TextField label="Serie" value={serie} onChange={(e) => setSerie(e.target.value)} disabled={submitting} />
              <TextField
                label="Número"
                value={numeroComprobante}
                onChange={(e) => setNumeroComprobante(e.target.value)}
                disabled={submitting}
              />
              <TextField
                label="Fecha emisión"
                type="date"
                value={fechaEmision}
                onChange={(e) => setFechaEmision(e.target.value)}
                InputLabelProps={{ shrink: true }}
                disabled={submitting}
              />
              <TextField select label="Moneda" value={moneda} onChange={(e) => setMoneda(e.target.value)} disabled={submitting}>
                {MONEDAS.map((m) => (
                  <MenuItem key={m.value} value={m.value}>
                    {m.label}
                  </MenuItem>
                ))}
              </TextField>
            </Box>
          </CardContent>
        </Card>

        <Card variant="outlined" sx={{ flex: 1 }}>
          <CardContent>
            <Autocomplete
              disabled={catalogLoading || submitting}
              options={productos}
              value={null}
              onChange={(_e, v) => agregarProducto(v)}
              getOptionLabel={(o) => `${o.codigo} — ${o.nombre}`}
              filterOptions={(opts, state) => {
                const q = state.inputValue.trim().toLowerCase();
                if (!q) return opts.slice(0, 50);
                return opts
                  .filter(
                    (p) =>
                      p.codigo.toLowerCase().includes(q) ||
                      p.nombre.toLowerCase().includes(q)
                  )
                  .slice(0, 40);
              }}
              renderInput={(params) => (
                <TextField
                  {...params}
                  inputRef={productSearchRef}
                  label="Buscar producto (código o nombre)"
                  placeholder="Escriba y Enter para agregar…"
                  autoFocus
                  sx={{
                    '& .MuiInputBase-root': {
                      fontSize: '1.15rem',
                      py: 0.5,
                      bgcolor: 'action.hover'
                    }
                  }}
                />
              )}
            />

            <TableContainer sx={{ mt: 2, border: 1, borderColor: 'divider', borderRadius: 2, maxHeight: 'min(52vh, 520px)' }}>
              <Table size="small" stickyHeader>
                <TableHead>
                  <TableRow>
                    <TableCell>Producto</TableCell>
                    <TableCell width={130}>Lote</TableCell>
                    <TableCell align="right" width={100}>
                      Cant.
                    </TableCell>
                    <TableCell align="right" width={110}>
                      P. unit.
                    </TableCell>
                    <TableCell align="right" width={110}>
                      Subtotal
                    </TableCell>
                    <TableCell width={48} />
                  </TableRow>
                </TableHead>
                <TableBody>
                  {carrito.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={6}>
                        <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 4 }}>
                          Carrito vacío — busque un producto arriba para agregarlo al instante.
                        </Typography>
                      </TableCell>
                    </TableRow>
                  ) : (
                    carrito.map((row) => (
                      <TableRow key={row.key} hover>
                        <TableCell>
                          <Typography variant="body2" fontWeight={600}>
                            {row.codigoProducto}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            {row.nombreProducto}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <TextField
                            select
                            size="small"
                            value={row.loteCodigo}
                            onChange={(e) => actualizarFila(row.key, { loteCodigo: e.target.value })}
                            disabled={submitting || row.lotesLoading}
                            fullWidth
                            SelectProps={{ displayEmpty: true }}
                          >
                            <MenuItem value="">
                              <em>{row.lotesLoading ? 'Cargando…' : 'Seleccione lote'}</em>
                            </MenuItem>
                            {row.lotesOpciones.map((lote) => (
                              <MenuItem key={lote.codigoLote} value={lote.codigoLote}>
                                {labelLoteOpcion(lote)}
                              </MenuItem>
                            ))}
                          </TextField>
                        </TableCell>
                        <TableCell align="right">
                          <TextField
                            size="small"
                            type="number"
                            value={row.cantidad}
                            onChange={(e) =>
                              actualizarFila(row.key, { cantidad: Number(e.target.value) || 0 })
                            }
                            inputProps={{ min: 0.0001, step: 'any', style: { textAlign: 'right' } }}
                            disabled={submitting}
                            sx={{ width: 88 }}
                          />
                        </TableCell>
                        <TableCell align="right">
                          <TextField
                            size="small"
                            type="number"
                            value={row.precioUnitario}
                            onChange={(e) =>
                              actualizarFila(row.key, { precioUnitario: Number(e.target.value) || 0 })
                            }
                            inputProps={{ min: 0, step: 'any', style: { textAlign: 'right' } }}
                            disabled={submitting}
                            sx={{ width: 100 }}
                          />
                        </TableCell>
                        <TableCell align="right" sx={{ whiteSpace: 'nowrap', fontWeight: 600 }}>
                          {fmtMoney(lineSubtotal(row), moneda)}
                        </TableCell>
                        <TableCell align="center">
                          <IconButton
                            size="small"
                            color="error"
                            aria-label="Quitar"
                            onClick={() => eliminarFila(row.key)}
                            disabled={submitting}
                          >
                            <DeleteOutlineRoundedIcon fontSize="small" />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>
      </Stack>

      <Card
        variant="outlined"
        sx={{
          position: { lg: 'sticky' },
          top: { lg: 16 },
          bgcolor: 'background.paper',
          borderColor: 'primary.light'
        }}
      >
        <CardContent>
          <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 2 }}>
            <PointOfSaleRoundedIcon color="primary" />
            <Typography variant="h6" fontWeight={700}>
              Totales
            </Typography>
          </Stack>

          <Stack spacing={1.5} sx={{ mb: 3 }}>
            <Stack direction="row" justifyContent="space-between">
              <Typography color="text.secondary">OP. GRAVADA</Typography>
              <Typography fontWeight={600}>{fmtMoney(totales.totalGravado, moneda)}</Typography>
            </Stack>
            <Stack direction="row" justifyContent="space-between">
              <Typography color="text.secondary">IGV (18%)</Typography>
              <Typography fontWeight={600}>{fmtMoney(totales.totalIgv, moneda)}</Typography>
            </Stack>
            <Stack
              direction="row"
              justifyContent="space-between"
              sx={{ pt: 1, borderTop: 1, borderColor: 'divider' }}
            >
              <Typography variant="subtitle1" fontWeight={800}>
                TOTAL
              </Typography>
              <Typography variant="h5" fontWeight={800} color="primary.main">
                {fmtMoney(totales.totalVenta, moneda)}
              </Typography>
            </Stack>
            <Typography variant="caption" color="text.secondary">
              {carrito.length} línea(s) · precios con IGV incluido
            </Typography>
          </Stack>

          <Button
            variant="contained"
            size="large"
            fullWidth
            disabled={submitting || carrito.length === 0}
            onClick={emitirComprobante}
            sx={{
              py: 2,
              fontSize: '1.1rem',
              fontWeight: 800,
              boxShadow: 4
            }}
          >
            {submitting ? 'Emitiendo…' : 'Emitir comprobante'}
          </Button>

          <Button
            variant="text"
            fullWidth
            sx={{ mt: 1 }}
            disabled={submitting}
            onClick={limpiarVenta}
          >
            Limpiar carrito
          </Button>
        </CardContent>
      </Card>

      <ClienteQuickCreateDialog
        open={clienteModalOpen}
        onClose={() => setClienteModalOpen(false)}
        onCreated={onClienteCreado}
      />

      <TicketImpresionDialog
        open={ticketVentaId != null}
        ventaId={ticketVentaId}
        onClose={() => setTicketVentaId(null)}
      />
    </Box>
  );
}
