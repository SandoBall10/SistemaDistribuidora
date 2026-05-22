import { Navigate, Route, Routes } from 'react-router-dom';
import { MainLayout } from '../layouts/MainLayout';
import { LoginPage } from '../pages/LoginPage';
import { DashboardPage } from '../pages/DashboardPage';
import { NotFoundPage } from '../pages/NotFoundPage';
import { UsuariosListPage } from '../pages/UsuariosListPage';
import { UsuarioCreatePage } from '../pages/UsuarioCreatePage';
import { UsuarioEditPage } from '../pages/UsuarioEditPage';
import { ProductosListPage } from '../pages/ProductosListPage';
import { ProductoFormPage } from '../pages/ProductoFormPage';
import { CompraFormPage } from '../pages/CompraFormPage';
import { VentaFormPage } from '../pages/VentaFormPage';
import { ClientesListPage } from '../pages/ClientesListPage';
import { ProveedorFormPage } from '../pages/ProveedorFormPage';
import { ProveedoresListPage } from '../pages/ProveedoresListPage';
import { PrivateRoute } from './PrivateRoute';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route element={<PrivateRoute />}>
        <Route element={<MainLayout />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route element={<PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_VENDEDOR']} />}>
            <Route path="/ventas" element={<VentaFormPage />} />
            <Route path="/clientes" element={<ClientesListPage />} />
            <Route path="/inventario/productos" element={<ProductosListPage />} />
            <Route path="/inventario/productos/nuevo" element={<ProductoFormPage />} />
            <Route path="/inventario/productos/:id/editar" element={<ProductoFormPage />} />
          </Route>
          <Route element={<PrivateRoute allowedRoles={['ROLE_ADMIN']} />}>
            <Route path="/compras/ingreso" element={<CompraFormPage />} />
            <Route path="/compras/proveedores" element={<ProveedoresListPage />} />
            <Route path="/compras/proveedores/nuevo" element={<ProveedorFormPage />} />
            <Route path="/compras/proveedores/:id/editar" element={<ProveedorFormPage />} />
            <Route path="/compras" element={<Navigate to="/compras/proveedores" replace />} />
            <Route path="/usuarios" element={<UsuariosListPage />} />
            <Route path="/usuarios/nuevo" element={<UsuarioCreatePage />} />
            <Route path="/usuarios/:id/editar" element={<UsuarioEditPage />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
