import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import PrintRoundedIcon from '@mui/icons-material/PrintRounded';
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Stack,
  Typography
} from '@mui/material';
import { useCallback, useEffect, useState } from 'react';
import { getVentaComprobante } from '../../services/ventaService';
import type { VentaComprobante } from '../../types/venta';
import { TicketImpresion } from './TicketImpresion';

interface TicketImpresionDialogProps {
  open: boolean;
  ventaId: number | null;
  onClose: () => void;
  onNuevaVenta?: () => void;
}

export function TicketImpresionDialog({
  open,
  ventaId,
  onClose,
  onNuevaVenta
}: TicketImpresionDialogProps) {
  const [comprobante, setComprobante] = useState<VentaComprobante | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!open || ventaId == null) {
      setComprobante(null);
      setError('');
      return;
    }
    let cancelled = false;
    (async () => {
      setLoading(true);
      setError('');
      try {
        const data = await getVentaComprobante(ventaId);
        if (!cancelled) setComprobante(data);
      } catch {
        if (!cancelled) setError('No se pudo cargar el comprobante para imprimir.');
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [open, ventaId]);

  const handlePrint = useCallback(() => {
    window.print();
  }, []);

  const handleClose = () => {
    onClose();
    onNuevaVenta?.();
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
      slotProps={{
        paper: {
          sx: {
            '@media print': {
              boxShadow: 'none',
              maxWidth: 'none',
              width: 'auto',
              m: 0
            }
          }
        }
      }}
    >
      <DialogTitle
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          '@media print': { display: 'none' }
        }}
      >
        Comprobante de pago
        <IconButton aria-label="Cerrar" onClick={onClose} size="small">
          <CloseRoundedIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent
        sx={{
          bgcolor: 'grey.100',
          display: 'flex',
          justifyContent: 'center',
          py: 3,
          '@media print': {
            bgcolor: '#fff',
            p: 0
          }
        }}
      >
        {loading ? (
          <Stack alignItems="center" py={4}>
            <CircularProgress size={32} />
            <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
              Generando ticket…
            </Typography>
          </Stack>
        ) : error ? (
          <Typography color="error">{error}</Typography>
        ) : comprobante ? (
          <TicketImpresion venta={comprobante} />
        ) : null}
      </DialogContent>

      <DialogActions
        sx={{
          px: 3,
          pb: 2,
          '@media print': { display: 'none' }
        }}
      >
        <Button onClick={onClose} color="inherit">
          Cerrar
        </Button>
        <Button
          variant="contained"
          startIcon={<PrintRoundedIcon />}
          onClick={handlePrint}
          disabled={!comprobante || loading}
        >
          Imprimir ticket
        </Button>
        {onNuevaVenta ? (
          <Button variant="outlined" onClick={handleClose}>
            Nueva venta
          </Button>
        ) : null}
      </DialogActions>
    </Dialog>
  );
}
