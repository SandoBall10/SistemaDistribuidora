import DashboardRoundedIcon from '@mui/icons-material/DashboardRounded';
import Inventory2OutlinedIcon from '@mui/icons-material/Inventory2Outlined';
import AddShoppingCartOutlinedIcon from '@mui/icons-material/AddShoppingCartOutlined';
import SellOutlinedIcon from '@mui/icons-material/SellOutlined';
import {
  Box,
  Divider,
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography
} from '@mui/material';
import { NavLink } from 'react-router-dom';

interface AppSidebarProps {
  open: boolean;
  width: number;
  onClose: () => void;
}

const menu = [
  { label: 'Dashboard', to: '/dashboard', icon: <DashboardRoundedIcon /> },
  { label: 'Ventas', to: '/ventas', icon: <SellOutlinedIcon /> },
  { label: 'Compras', to: '/compras', icon: <AddShoppingCartOutlinedIcon /> },
  { label: 'Inventario', to: '/inventario', icon: <Inventory2OutlinedIcon /> }
];

export function AppSidebar({ open, width, onClose }: AppSidebarProps) {
  return (
    <Drawer
      variant="persistent"
      anchor="left"
      open={open}
      onClose={onClose}
      PaperProps={{ sx: { width, backgroundColor: 'background.paper' } }}
    >
      <Toolbar />
      <Box px={2} py={1}>
        <Typography variant="overline" color="text.secondary">
          Modulos ERP
        </Typography>
      </Box>
      <Divider />
      <List sx={{ px: 1.5 }}>
        {menu.map((item) => (
          <ListItemButton
            key={item.to}
            component={NavLink}
            to={item.to}
            onClick={onClose}
            sx={{
              borderRadius: 2,
              mb: 0.5,
              '&.active': {
                backgroundColor: 'primary.main',
                color: 'primary.contrastText',
                '& .MuiListItemIcon-root': { color: 'primary.contrastText' }
              }
            }}
          >
            <ListItemIcon>{item.icon}</ListItemIcon>
            <ListItemText primary={item.label} />
          </ListItemButton>
        ))}
      </List>
    </Drawer>
  );
}
