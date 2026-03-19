import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import MenuRoundedIcon from '@mui/icons-material/MenuRounded';
import { AppBar, Box, IconButton, Toolbar, Typography, Tooltip } from '@mui/material';
import { useAuth } from '../../hooks/useAuth';

interface AppNavbarProps {
  onToggleSidebar: () => void;
}

export function AppNavbar({ onToggleSidebar }: AppNavbarProps) {
  const { logout, session } = useAuth();

  return (
    <AppBar
      position="fixed"
      color="inherit"
      sx={{ borderBottom: '1px solid', borderColor: 'divider', zIndex: (theme) => theme.zIndex.drawer + 1 }}
    >
      <Toolbar>
        <IconButton edge="start" color="inherit" onClick={onToggleSidebar} sx={{ mr: 2 }}>
          <MenuRoundedIcon />
        </IconButton>
        <Typography variant="h6" color="text.primary" sx={{ flexGrow: 1 }}>
          ERP Distribuidora
        </Typography>
        <Box sx={{ mr: 2 }}>
          <Typography variant="caption" color="text.secondary">
            Usuario
          </Typography>
          <Typography variant="body2">{session?.nombreUsuario ?? 'N/A'}</Typography>
        </Box>
        <Tooltip title="Cerrar sesión">
          <IconButton color="inherit" onClick={logout}>
            <LogoutOutlinedIcon />
          </IconButton>
        </Tooltip>
      </Toolbar>
    </AppBar>
  );
}
