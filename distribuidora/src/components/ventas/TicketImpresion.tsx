import { Box, Typography } from '@mui/material';
import type { VentaComprobante } from '../../types/venta';

const EMPRESA = {
  nombre: 'DISTRIBUIDORA LIDER',
  ruc: '20123456789',
  direccion: 'Av. Los Industriales 1280, Lima',
  telefono: '(01) 456-7890'
};

function fmtMoney(n: number, moneda: string) {
  const cur = moneda === 'USD' ? 'USD' : 'PEN';
  return new Intl.NumberFormat('es-PE', { style: 'currency', currency: cur }).format(n);
}

function fmtCantidad(n: number) {
  return Number.isInteger(n) ? String(n) : n.toFixed(2);
}

function fmtFechaHora(fechaEmision: string, fechaCreacion: string) {
  const emision = fechaEmision ? new Date(fechaEmision + 'T12:00:00') : null;
  const creacion = fechaCreacion ? new Date(fechaCreacion) : new Date();
  const fecha = emision
    ? emision.toLocaleDateString('es-PE', { day: '2-digit', month: '2-digit', year: 'numeric' })
    : creacion.toLocaleDateString('es-PE');
  const hora = creacion.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });
  return `${fecha} ${hora}`;
}

function dashLine() {
  return (
    <Box
      sx={{
        borderBottom: '1px dashed #333',
        my: 1.25,
        opacity: 0.65
      }}
    />
  );
}

interface TicketImpresionProps {
  venta: VentaComprobante;
  className?: string;
}

export function TicketImpresion({ venta, className }: TicketImpresionProps) {
  const moneda = venta.monedaCodigo ?? 'PEN';
  const cliente =
    venta.clienteNombre?.trim() ||
    venta.clienteCodigo ||
    `Cliente #${venta.clienteId}`;
  const doc = venta.clienteNumeroDocumento ? ` — ${venta.clienteNumeroDocumento}` : '';
  const comprobanteLabel = venta.tipoComprobanteCodigo === 'FACTURA' ? 'FACTURA' : 'BOLETA';
  const numeroFull = `${venta.serie}-${venta.numeroComprobante}`;

  return (
    <Box
      className={className ?? 'ticket-print-area'}
      sx={{
        width: '80mm',
        maxWidth: '300px',
        minWidth: '280px',
        mx: 'auto',
        bgcolor: '#fff',
        color: '#111',
        fontFamily: '"IBM Plex Mono", "Roboto Mono", "Consolas", monospace',
        fontSize: '11px',
        lineHeight: 1.45,
        p: 2,
        boxShadow: { xs: 'none', md: '0 8px 32px rgba(0,0,0,0.12)' },
        borderRadius: { xs: 0, md: 1 },
        '@media print': {
          boxShadow: 'none',
          borderRadius: 0,
          p: '4mm',
          width: '80mm',
          maxWidth: '80mm'
        }
      }}
    >
      <Typography
        align="center"
        sx={{
          fontWeight: 800,
          fontSize: '13px',
          letterSpacing: '0.04em',
          fontFamily: '"Poppins", sans-serif',
          textTransform: 'uppercase',
          mb: 0.5
        }}
      >
        {EMPRESA.nombre}
      </Typography>
      <Typography align="center" sx={{ fontSize: '10px', color: '#444' }}>
        RUC {EMPRESA.ruc}
      </Typography>
      <Typography align="center" sx={{ fontSize: '10px', color: '#444' }}>
        {EMPRESA.direccion}
      </Typography>
      <Typography align="center" sx={{ fontSize: '10px', color: '#444', mb: 0.5 }}>
        Tel. {EMPRESA.telefono}
      </Typography>

      {dashLine()}

      <Typography sx={{ fontWeight: 700, fontSize: '11px', textAlign: 'center' }}>
        {comprobanteLabel} DE VENTA
      </Typography>
      <Typography align="center" sx={{ fontWeight: 600, fontSize: '12px', my: 0.5 }}>
        {numeroFull}
      </Typography>

      <Box sx={{ display: 'grid', gap: 0.35 }}>
        <Row label="Fecha/Hora" value={fmtFechaHora(venta.fechaEmision, venta.fechaCreacion)} />
        <Row label="Cliente" value={`${cliente}${doc}`} />
        <Row label="Moneda" value={moneda} />
      </Box>

      {dashLine()}

      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: '28px 1fr 62px',
          gap: 0.5,
          fontWeight: 700,
          fontSize: '9px',
          textTransform: 'uppercase',
          color: '#555',
          mb: 0.5
        }}
      >
        <span>CANT</span>
        <span>DESCRIPCIÓN</span>
        <span style={{ textAlign: 'right' }}>IMPORTE</span>
      </Box>

      {venta.detalles.map((d) => (
        <Box
          key={d.id}
          sx={{
            display: 'grid',
            gridTemplateColumns: '28px 1fr 62px',
            gap: 0.5,
            py: 0.35,
            borderBottom: '1px dotted #ddd'
          }}
        >
          <span>{fmtCantidad(Number(d.cantidad))}</span>
          <Box sx={{ overflow: 'hidden' }}>
            <Typography sx={{ fontSize: '10px', fontWeight: 600, lineHeight: 1.2 }}>
              {d.productoNombre}
            </Typography>
            <Typography sx={{ fontSize: '9px', color: '#666' }}>{d.productoCodigo}</Typography>
          </Box>
          <span style={{ textAlign: 'right', fontWeight: 600 }}>
            {fmtMoney(Number(d.subtotal), moneda)}
          </span>
        </Box>
      ))}

      {dashLine()}

      <Box sx={{ textAlign: 'right', pr: 0.5 }}>
        <TotalRow label="OP. GRAVADA" value={fmtMoney(Number(venta.totalGravado), moneda)} />
        <TotalRow label="IGV (18%)" value={fmtMoney(Number(venta.totalIgv), moneda)} />
        <Box sx={{ mt: 0.75, pt: 0.75, borderTop: '2px solid #111' }}>
          <Typography sx={{ fontWeight: 800, fontSize: '13px' }}>
            TOTAL {fmtMoney(Number(venta.totalVenta), moneda)}
          </Typography>
        </Box>
      </Box>

      {dashLine()}

      <Typography
        align="center"
        sx={{
          fontSize: '10px',
          fontStyle: 'italic',
          fontFamily: '"Poppins", sans-serif',
          color: '#333',
          mt: 0.5
        }}
      >
        ¡Gracias por su preferencia!
      </Typography>
      <Typography align="center" sx={{ fontSize: '9px', color: '#888', mt: 0.5 }}>
        Comprobante #{venta.id}
      </Typography>
    </Box>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <Box sx={{ display: 'flex', gap: 0.5 }}>
      <Typography component="span" sx={{ fontSize: '10px', color: '#666', minWidth: 72 }}>
        {label}:
      </Typography>
      <Typography component="span" sx={{ fontSize: '10px', fontWeight: 600, flex: 1 }}>
        {value}
      </Typography>
    </Box>
  );
}

function TotalRow({ label, value }: { label: string; value: string }) {
  return (
    <Typography sx={{ fontSize: '10px', mb: 0.25 }}>
      <Box component="span" sx={{ color: '#555', mr: 1 }}>
        {label}
      </Box>
      <Box component="span" sx={{ fontWeight: 700 }}>
        {value}
      </Box>
    </Typography>
  );
}
