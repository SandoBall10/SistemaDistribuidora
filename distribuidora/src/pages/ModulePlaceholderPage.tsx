import { Card, CardContent, Stack, Typography } from '@mui/material';

interface ModulePlaceholderPageProps {
  title: string;
  description: string;
}

export function ModulePlaceholderPage({ title, description }: ModulePlaceholderPageProps) {
  return (
    <Card>
      <CardContent>
        <Stack spacing={1}>
          <Typography variant="h5">{title}</Typography>
          <Typography color="text.secondary">{description}</Typography>
        </Stack>
      </CardContent>
    </Card>
  );
}
