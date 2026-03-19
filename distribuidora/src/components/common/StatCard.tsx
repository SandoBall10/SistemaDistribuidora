import ShowChartIcon from '@mui/icons-material/ShowChart';
import { Card, CardContent, Stack, Typography } from '@mui/material';

interface StatCardProps {
  title: string;
  value: string;
  subtitle: string;
}

export function StatCard({ title, value, subtitle }: StatCardProps) {
  return (
    <Card>
      <CardContent>
        <Stack direction="row" justifyContent="space-between" mb={2}>
          <Typography variant="subtitle2" color="text.secondary">
            {title}
          </Typography>
          <ShowChartIcon color="primary" fontSize="small" />
        </Stack>
        <Typography variant="h4">{value}</Typography>
        <Typography variant="body2" color="text.secondary" mt={1}>
          {subtitle}
        </Typography>
      </CardContent>
    </Card>
  );
}
