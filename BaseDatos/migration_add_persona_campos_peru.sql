-- Campos SUNAT / identidad para personas (Perú)
BEGIN;

ALTER TABLE erp.personas
  ADD COLUMN IF NOT EXISTS nombre_comercial VARCHAR(200);

ALTER TABLE erp.personas
  ADD COLUMN IF NOT EXISTS estado_sunat VARCHAR(40);

ALTER TABLE erp.personas
  ADD COLUMN IF NOT EXISTS condicion_sunat VARCHAR(40);

ALTER TABLE erp.personas
  ADD COLUMN IF NOT EXISTS genero CHAR(1);

ALTER TABLE erp.personas
  ADD COLUMN IF NOT EXISTS es_contribuyente BOOLEAN NOT NULL DEFAULT FALSE;

COMMIT;
