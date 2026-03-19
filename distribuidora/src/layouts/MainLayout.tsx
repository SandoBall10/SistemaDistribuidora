import { useEffect, useState } from 'react';
import { Box, Toolbar, useMediaQuery, useTheme } from '@mui/material';
import { Outlet } from 'react-router-dom';
import { AppNavbar } from '../components/navigation/AppNavbar';
import { AppSidebar } from '../components/navigation/AppSidebar';

const SIDEBAR_WIDTH = 260;

export function MainLayout() {
  const theme = useTheme();
  const isDesktop = useMediaQuery(theme.breakpoints.up('lg'));
  const [sidebarOpen, setSidebarOpen] = useState(true);

  useEffect(() => {
    setSidebarOpen(isDesktop);
  }, [isDesktop]);

  const handleToggleSidebar = () => {
    setSidebarOpen((prev) => !prev);
  };

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', backgroundColor: 'background.default' }}>
      <AppNavbar onToggleSidebar={handleToggleSidebar} />
      <AppSidebar open={sidebarOpen} width={SIDEBAR_WIDTH} onClose={() => setSidebarOpen(false)} />

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          transition: theme.transitions.create('margin', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.standard
          }),
          ml: { lg: sidebarOpen ? `${SIDEBAR_WIDTH}px` : 0 },
          p: 3
        }}
      >
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
}
