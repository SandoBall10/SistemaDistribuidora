import {
  Box,
  Card,
  CardContent,
  Chip,
  Grid,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography
} from '@mui/material';
import { StatCard } from '../components/common/StatCard';

const latestSales = [
  { id: 'FAC-10045', cliente: 'Supermercado Centro', total: 1540.5, estado: 'Pagado' },
  { id: 'FAC-10046', cliente: 'Farmacia Norte', total: 920.0, estado: 'Pendiente' },
  { id: 'FAC-10047', cliente: 'Minimarket Sur', total: 2480.7, estado: 'Pagado' }
];

export function DashboardPage() {
  return (
    <Stack spacing={3}>
      <Box>
        <Typography variant="h4">Dashboard</Typography>
        <Typography variant="body2" color="text.secondary">
          Vista general de operaciones para el ERP de la distribuidora.
        </Typography>
      </Box>

      <Grid container spacing={2.5}>
        <Grid size={{ xs: 12, md: 4 }}>
          <StatCard title="Ventas del día" value="S/ 14,230" subtitle="+12.4% vs ayer" />
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <StatCard title="Órdenes pendientes" value="37" subtitle="8 por despachar hoy" />
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <StatCard title="Stock crítico" value="15 SKU" subtitle="Requiere reposición inmediata" />
        </Grid>
      </Grid>

      <Card>
        <CardContent>
          <Typography variant="h6" mb={2}>
            Últimas facturas
          </Typography>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Documento</TableCell>
                <TableCell>Cliente</TableCell>
                <TableCell align="right">Total</TableCell>
                <TableCell>Estado</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {latestSales.map((row) => (
                <TableRow key={row.id} hover>
                  <TableCell>{row.id}</TableCell>
                  <TableCell>{row.cliente}</TableCell>
                  <TableCell align="right">S/ {row.total.toFixed(2)}</TableCell>
                  <TableCell>
                    <Chip
                      size="small"
                      label={row.estado}
                      color={row.estado === 'Pagado' ? 'success' : 'warning'}
                      variant="outlined"
                    />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </Stack>
  );
}
