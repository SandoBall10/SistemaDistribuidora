import PrintRoundedIcon from '@mui/icons-material/PrintRounded';
import ReceiptLongRoundedIcon from '@mui/icons-material/ReceiptLongRounded';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography
} from '@mui/material';
import { useCallback, useEffect, useState } from 'react';
import { TicketImpresionDialog } from '../components/ventas/TicketImpresionDialog';
import { listVentas } from '../services/ventaService';
import type { VentaResponse } from '../types/venta';

function fmtMoney(n: number, moneda: string) {
  const cur = moneda === 'USD' ? 'USD' : 'PEN';
  return new Intl.NumberFormat('es-PE', { style: 'currency', currency: cur }).format(n);
}

function fmtFecha(iso: string) {
  try {
    return new Date(iso).toLocaleString('es-PE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch {
    return iso;
  }
}

export function VentasHistorialPage() {
  const [ventas, setVentas] = useState<VentaResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [ticketVentaId, setTicketVentaId] = useState<number | null>(null);

  const cargar = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await listVentas();
      setVentas(data);
    } catch {
      setError('No se pudo cargar el historial de ventas.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void cargar();
  }, [cargar]);

  return (
    <Stack spacing={2}>
      <Stack direction="row" alignItems="center" spacing={1}>
        <ReceiptLongRoundedIcon color="primary" />
        <Typography variant="h5" fontWeight={700}>
          Historial de ventas
        </Typography>
      </Stack>

      {error ? <Alert severity="error">{error}</Alert> : null}

      <Card variant="outlined">
        <CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}>
          {loading ? (
            <Box py={6} display="flex" justifyContent="center">
              <CircularProgress />
            </Box>
          ) : ventas.length === 0 ? (
            <Typography color="text.secondary" align="center" py={4}>
              Aún no hay ventas registradas.
            </Typography>
          ) : (
            <TableContainer>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Fecha</TableCell>
                    <TableCell>Comprobante</TableCell>
                    <TableCell>Tipo</TableCell>
                    <TableCell align="right">Total</TableCell>
                    <TableCell align="center" width={140}>
                      Ticket
                    </TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {ventas.map((v) => (
                    <TableRow key={v.id} hover>
                      <TableCell>{fmtFecha(v.fechaCreacion)}</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>
                        {v.serie}-{v.numeroComprobante}
                      </TableCell>
                      <TableCell>{v.tipoComprobanteCodigo}</TableCell>
                      <TableCell align="right">
                        {fmtMoney(Number(v.totalVenta), v.monedaCodigo)}
                      </TableCell>
                      <TableCell align="center">
                        <Button
                          size="small"
                          variant="outlined"
                          startIcon={<PrintRoundedIcon />}
                          onClick={() => setTicketVentaId(v.id)}
                        >
                          Ver / Imprimir
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>

      <TicketImpresionDialog
        open={ticketVentaId != null}
        ventaId={ticketVentaId}
        onClose={() => setTicketVentaId(null)}
      />
    </Stack>
  );
}
