-- Precio de venta unitario en catálogo (POS y listados)
ALTER TABLE erp.productos
  ADD COLUMN IF NOT EXISTS precio_venta NUMERIC(18,4) DEFAULT 0;

UPDATE erp.productos SET precio_venta = 0 WHERE precio_venta IS NULL;

ALTER TABLE erp.productos
  ALTER COLUMN precio_venta SET NOT NULL,
  ALTER COLUMN precio_venta SET DEFAULT 0;

ALTER TABLE erp.productos
  DROP CONSTRAINT IF EXISTS chk_productos_precio_venta;

ALTER TABLE erp.productos
  ADD CONSTRAINT chk_productos_precio_venta CHECK (precio_venta >= 0);
