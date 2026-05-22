-- Migración: añade afectación IGV a productos (bases de datos creadas antes de esta columna)
-- Ejecutar una sola vez en PostgreSQL.

BEGIN;

ALTER TABLE erp.productos
  ADD COLUMN IF NOT EXISTS tipo_igv_id BIGINT;

-- Asigna un tipo IGV activo por defecto a filas existentes
UPDATE erp.productos p
SET tipo_igv_id = (SELECT c.id FROM erp.catalogo_tipo_igv c WHERE c.activo = TRUE ORDER BY c.id LIMIT 1)
WHERE p.tipo_igv_id IS NULL;

-- Si aún quedan NULL, no se puede NOT NULL: cree al menos un registro en erp.catalogo_tipo_igv
ALTER TABLE erp.productos
  ALTER COLUMN tipo_igv_id SET NOT NULL;

ALTER TABLE erp.productos
  DROP CONSTRAINT IF EXISTS fk_productos_tipo_igv;
ALTER TABLE erp.productos
  ADD CONSTRAINT fk_productos_tipo_igv
  FOREIGN KEY (tipo_igv_id) REFERENCES erp.catalogo_tipo_igv(id);

COMMIT;
