-- =============================================================================
-- migration_fix_esquema_ventas.sql
-- =============================================================================
-- Objetivo: reproducir en PostgreSQL los parches manuales aplicados durante las
-- pruebas del Punto de Venta (POST /api/ventas), alineando el esquema legacy
-- con las entidades JPA actuales (Venta, VentaDetalle).
--
-- Cuándo ejecutar:
--   - Bases de datos que ya tenían erp.ventas / erp.ventas_detalle con un
--     diseño anterior (columnas correlativo, tipo_comprobante, sin auditoría).
--   - Después de migration_ventas.sql si CREATE TABLE IF NOT EXISTS no añadió
--     columnas nuevas a tablas ya existentes.
--
-- Es idempotente: puede ejecutarse más de una vez sin fallar.
-- =============================================================================

BEGIN;

-- -----------------------------------------------------------------------------
-- 1. erp.ventas — columnas de comprobante, moneda y auditoría
-- -----------------------------------------------------------------------------

-- Parches aplicados en QA (idempotentes con IF NOT EXISTS):
ALTER TABLE erp.ventas
  ADD COLUMN IF NOT EXISTS fecha_modificacion TIMESTAMP;

ALTER TABLE erp.ventas
  ADD COLUMN IF NOT EXISTS moneda_codigo VARCHAR(3) DEFAULT 'PEN';

ALTER TABLE erp.ventas
  ADD COLUMN IF NOT EXISTS numero_comprobante VARCHAR(50);

ALTER TABLE erp.ventas
  ADD COLUMN IF NOT EXISTS tipo_comprobante_codigo VARCHAR(20);

ALTER TABLE erp.ventas
  ADD COLUMN IF NOT EXISTS usuario_modificacion VARCHAR(50);

-- Compatibilidad con esquema legacy: relajar NOT NULL en columnas obsoletas
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'erp' AND table_name = 'ventas' AND column_name = 'tipo_comprobante'
  ) THEN
    ALTER TABLE erp.ventas ALTER COLUMN tipo_comprobante DROP NOT NULL;
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'erp' AND table_name = 'ventas' AND column_name = 'correlativo'
  ) THEN
    ALTER TABLE erp.ventas ALTER COLUMN correlativo DROP NOT NULL;
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'erp' AND table_name = 'ventas' AND column_name = 'serie'
  ) THEN
    ALTER TABLE erp.ventas ALTER COLUMN serie DROP NOT NULL;
  END IF;
END $$;

-- Valores por defecto en filas existentes (evita NULL en persistencia JPA)
UPDATE erp.ventas
SET moneda_codigo = COALESCE(moneda_codigo, 'PEN')
WHERE moneda_codigo IS NULL;

UPDATE erp.ventas
SET fecha_modificacion = COALESCE(fecha_modificacion, fecha_creacion, now())
WHERE fecha_modificacion IS NULL;

-- -----------------------------------------------------------------------------
-- 2. erp.ventas_detalle — columnas de auditoría
-- -----------------------------------------------------------------------------

ALTER TABLE erp.ventas_detalle
  ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP;

ALTER TABLE erp.ventas_detalle
  ADD COLUMN IF NOT EXISTS fecha_modificacion TIMESTAMP;

ALTER TABLE erp.ventas_detalle
  ADD COLUMN IF NOT EXISTS usuario_creacion VARCHAR(50);

ALTER TABLE erp.ventas_detalle
  ADD COLUMN IF NOT EXISTS usuario_modificacion VARCHAR(50);

UPDATE erp.ventas_detalle
SET fecha_creacion = COALESCE(fecha_creacion, now())
WHERE fecha_creacion IS NULL;

UPDATE erp.ventas_detalle
SET fecha_modificacion = COALESCE(fecha_modificacion, fecha_creacion, now())
WHERE fecha_modificacion IS NULL;

COMMIT;

-- =============================================================================
-- Notas post-migración
-- =============================================================================
-- • En instalaciones nuevas use script.sql (CREATE TABLE con TIMESTAMPTZ y
--   longitudes alineadas a JPA). Este archivo reproduce los ALTER de QA.
-- • tipo_comprobante_codigo / numero_comprobante / serie son los campos del POS;
--   correlativo y tipo_comprobante (legacy) quedan opcionales si aún existen.
-- =============================================================================
