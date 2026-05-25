import DashboardRoundedIcon from '@mui/icons-material/DashboardRounded';
import Inventory2OutlinedIcon from '@mui/icons-material/Inventory2Outlined';
import AddShoppingCartOutlinedIcon from '@mui/icons-material/AddShoppingCartOutlined';
import ExpandLessRoundedIcon from '@mui/icons-material/ExpandLessRounded';
import ExpandMoreRoundedIcon from '@mui/icons-material/ExpandMoreRounded';
import ReceiptLongRoundedIcon from '@mui/icons-material/ReceiptLongRounded';
import SellOutlinedIcon from '@mui/icons-material/SellOutlined';
import PeopleOutlineRoundedIcon from '@mui/icons-material/PeopleOutlineRounded';
import ManageAccountsOutlinedIcon from '@mui/icons-material/ManageAccountsOutlined';
import InventoryOutlinedIcon from '@mui/icons-material/InventoryOutlined';
import LocalShippingOutlinedIcon from '@mui/icons-material/LocalShippingOutlined';
import {
  Box,
  Collapse,
  Divider,
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography
} from '@mui/material';
import { useEffect, useState, type ReactNode } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

interface AppSidebarProps {
  open: boolean;
  width: number;
  onClose: () => void;
}

const NavItem = ({
  to,
  label,
  icon,
  onNavigate
}: {
  to: string;
  label: string;
  icon: ReactNode;
  onNavigate: () => void;
}) => (
  <ListItemButton
    component={NavLink}
    to={to}
    onClick={onNavigate}
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
    <ListItemIcon>{icon}</ListItemIcon>
    <ListItemText primary={label} />
  </ListItemButton>
);

export function AppSidebar({ open, width, onClose }: AppSidebarProps) {
  const { session } = useAuth();
  const { pathname } = useLocation();

  const isVendedor = session?.rol === 'ROLE_VENDEDOR';
  const isAdmin = session?.rol === 'ROLE_ADMIN';

  const [comprasOpen, setComprasOpen] = useState(() => pathname.startsWith('/compras'));

  useEffect(() => {
    if (pathname.startsWith('/compras')) {
      setComprasOpen(true);
    }
  }, [pathname]);

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
        <NavItem to="/dashboard" label="Dashboard" icon={<DashboardRoundedIcon />} onNavigate={onClose} />
        {(isAdmin || isVendedor) && (
          <>
            <NavItem to="/ventas" label="Punto de venta" icon={<SellOutlinedIcon />} onNavigate={onClose} />
            <NavItem
              to="/ventas/historial"
              label="Historial ventas"
              icon={<ReceiptLongRoundedIcon />}
              onNavigate={onClose}
            />
            <NavItem to="/clientes" label="Clientes" icon={<PeopleOutlineRoundedIcon />} onNavigate={onClose} />
          </>
        )}

        {!isVendedor && (
          <>
            <ListItemButton
              onClick={() => setComprasOpen(!comprasOpen)}
              sx={{
                borderRadius: 2,
                mb: 0.5,
                ...(pathname.startsWith('/compras')
                  ? {
                      backgroundColor: 'primary.main',
                      color: 'primary.contrastText',
                      '& .MuiListItemIcon-root': { color: 'primary.contrastText' }
                    }
                  : {})
              }}
            >
              <ListItemIcon>
                <AddShoppingCartOutlinedIcon />
              </ListItemIcon>
              <ListItemText primary="Compras" />
              {comprasOpen ? <ExpandLessRoundedIcon /> : <ExpandMoreRoundedIcon />}
            </ListItemButton>
            <Collapse in={comprasOpen} timeout="auto" unmountOnExit>
              <List component="div" disablePadding>
                <ListItemButton
                  component={NavLink}
                  to="/compras/ingreso"
                  onClick={onClose}
                  sx={{
                    pl: 3,
                    borderRadius: 2,
                    mb: 0.5,
                    '&.active': {
                      backgroundColor: 'primary.main',
                      color: 'primary.contrastText',
                      '& .MuiListItemIcon-root': { color: 'primary.contrastText' }
                    }
                  }}
                >
                  <ListItemIcon sx={{ minWidth: 40 }}>
                    <InventoryOutlinedIcon fontSize="small" />
                  </ListItemIcon>
                  <ListItemText primary="Ingreso de productos" />
                </ListItemButton>
                <ListItemButton
                  component={NavLink}
                  to="/compras/proveedores"
                  onClick={onClose}
                  sx={{
                    pl: 3,
                    borderRadius: 2,
                    mb: 0.5,
                    '&.active': {
                      backgroundColor: 'primary.main',
                      color: 'primary.contrastText',
                      '& .MuiListItemIcon-root': { color: 'primary.contrastText' }
                    }
                  }}
                >
                  <ListItemIcon sx={{ minWidth: 40 }}>
                    <LocalShippingOutlinedIcon fontSize="small" />
                  </ListItemIcon>
                  <ListItemText primary="Proveedores" />
                </ListItemButton>
              </List>
            </Collapse>
          </>
        )}

        <NavItem
          to="/inventario/productos"
          label="Inventario"
          icon={<Inventory2OutlinedIcon />}
          onNavigate={onClose}
        />

        {isAdmin && (
          <NavItem
            to="/usuarios"
            label="Gestión de Usuarios"
            icon={<ManageAccountsOutlinedIcon />}
            onNavigate={onClose}
          />
        )}
      </List>
    </Drawer>
  );
}
