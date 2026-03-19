import { Navigate, Route, Routes } from 'react-router-dom';
import { MainLayout } from '../layouts/MainLayout';
import { LoginPage } from '../pages/LoginPage';
import { DashboardPage } from '../pages/DashboardPage';
import { NotFoundPage } from '../pages/NotFoundPage';
import { ModulePlaceholderPage } from '../pages/ModulePlaceholderPage';
import { PrivateRoute } from './PrivateRoute';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route element={<PrivateRoute />}>
        <Route element={<MainLayout />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route
            path="/ventas"
            element={<ModulePlaceholderPage title="Ventas" description="Base lista para pedidos, cotizaciones y facturación." />}
          />
          <Route
            path="/compras"
            element={<ModulePlaceholderPage title="Compras" description="Base lista para órdenes, proveedores y recepciones." />}
          />
          <Route
            path="/inventario"
            element={<ModulePlaceholderPage title="Inventario" description="Base lista para stocks, movimientos y kardex." />}
          />
        </Route>
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
