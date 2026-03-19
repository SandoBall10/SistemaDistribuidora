import { Button, Container, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <Container maxWidth="sm" sx={{ py: 8 }}>
      <Stack spacing={2} alignItems="center">
        <Typography variant="h3">404</Typography>
        <Typography variant="h6">Página no encontrada</Typography>
        <Button component={Link} to="/dashboard" variant="contained">
          Volver al dashboard
        </Button>
      </Stack>
    </Container>
  );
}
