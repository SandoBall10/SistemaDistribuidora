-- Columnas comerciales en proveedores (ejecutar sobre BD existentes)
BEGIN;

ALTER TABLE erp.proveedores
  ADD COLUMN IF NOT EXISTS plazo_credito_dias INTEGER CHECK (plazo_credito_dias IS NULL OR plazo_credito_dias >= 0),
  ADD COLUMN IF NOT EXISTS cuenta_soles VARCHAR(40);

COMMIT;
