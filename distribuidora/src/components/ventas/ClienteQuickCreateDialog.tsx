import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import {
  Alert,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  InputAdornment,
  MenuItem,
  Stack,
  TextField
} from '@mui/material';
import { useCallback, useState } from 'react';
import { isAxiosError } from 'axios';
import { createCliente } from '../../services/clienteService';
import {
  consultarDocumentoExterno,
  mensajeErrorConsultaDocumento
} from '../../services/consultaExternaService';
import { extractValidationErrors } from '../../services/validation';
import type { ClienteResponse } from '../../types/cliente';

type DocTipo = 'DNI' | 'RUC';

type Props = {
  open: boolean;
  onClose: () => void;
  onCreated: (cliente: ClienteResponse) => void;
};

export function ClienteQuickCreateDialog({ open, onClose, onCreated }: Props) {
  const [tipoDocumento, setTipoDocumento] = useState<DocTipo>('DNI');
  const [numeroDocumento, setNumeroDocumento] = useState('');
  const [razonSocialNombre, setRazonSocialNombre] = useState('');
  const [direccion, setDireccion] = useState('');
  const [nombres, setNombres] = useState('');
  const [apellidoPaterno, setApellidoPaterno] = useState('');
  const [apellidoMaterno, setApellidoMaterno] = useState('');
  const [estadoSunat, setEstadoSunat] = useState('');
  const [condicionSunat, setCondicionSunat] = useState('');

  const [searchLoading, setSearchLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [info, setInfo] = useState('');

  const maxLen = tipoDocumento === 'DNI' ? 8 : 11;

  const resetForm = useCallback(() => {
    setTipoDocumento('DNI');
    setNumeroDocumento('');
    setRazonSocialNombre('');
    setDireccion('');
    setNombres('');
    setApellidoPaterno('');
    setApellidoMaterno('');
    setEstadoSunat('');
    setCondicionSunat('');
    setError('');
    setInfo('');
  }, []);

  const handleClose = useCallback(() => {
    if (saving) return;
    resetForm();
    onClose();
  }, [onClose, resetForm, saving]);

  const buscarDocumento = useCallback(async () => {
    const doc = numeroDocumento.replace(/\D/g, '');
    if ((tipoDocumento === 'DNI' && doc.length !== 8) || (tipoDocumento === 'RUC' && doc.length !== 11)) {
      setError(`Ingrese un ${tipoDocumento} válido (${tipoDocumento === 'DNI' ? '8' : '11'} dígitos) antes de consultar.`);
      setInfo('');
      return;
    }

    setError('');
    setInfo('');
    setSearchLoading(true);

    try {
      const data = await consultarDocumentoExterno(tipoDocumento, doc);
      let datosCargados = false;

      if (tipoDocumento === 'RUC') {
        if (data.razonSocial) {
          setRazonSocialNombre(data.razonSocial);
          datosCargados = true;
        }
        if (data.direccion) {
          setDireccion(data.direccion);
          datosCargados = true;
        }
        if (data.estadoSunat) {
          setEstadoSunat(data.estadoSunat);
        }
        if (data.condicionSunat) {
          setCondicionSunat(data.condicionSunat);
        }
      } else {
        if (data.nombres) {
          setNombres(data.nombres);
          datosCargados = true;
        }
        if (data.apellidoPaterno) {
          setApellidoPaterno(data.apellidoPaterno);
          datosCargados = true;
        }
        if (data.apellidoMaterno) {
          setApellidoMaterno(data.apellidoMaterno);
          datosCargados = true;
        }
        const nombre =
          [data.nombres, data.apellidoPaterno, data.apellidoMaterno].filter(Boolean).join(' ').trim() ||
          data.razonSocial ||
          '';
        if (nombre) {
          setRazonSocialNombre(nombre);
          datosCargados = true;
        }
        if (data.direccion) {
          setDireccion(data.direccion);
          datosCargados = true;
        }
        if (data.estadoSunat) {
          setEstadoSunat(data.estadoSunat);
        }
      }

      if (datosCargados) {
        setInfo('Datos obtenidos de RENIEC/SUNAT correctamente.');
      } else {
        setInfo('Consulta exitosa, pero el proveedor no devolvió nombre ni dirección. Complete manualmente.');
      }
    } catch (err) {
      console.error('[ClienteQuickCreate] Error consulta documento:', err);
      if (isAxiosError(err)) {
        console.error('[ClienteQuickCreate] HTTP', err.response?.status, err.response?.data);
      }
      setError(mensajeErrorConsultaDocumento(err));
      setInfo('');
    } finally {
      setSearchLoading(false);
    }
  }, [numeroDocumento, tipoDocumento]);

  const guardar = useCallback(async () => {
    setError('');
    setInfo('');
    const doc = numeroDocumento.replace(/\D/g, '');
    if (!razonSocialNombre.trim()) {
      setError('Indique el nombre o razón social.');
      return;
    }
    try {
      setSaving(true);
      const created = await createCliente({
        tipoDocumento,
        numeroDocumento: doc,
        razonSocialNombre: razonSocialNombre.trim(),
        direccion: direccion.trim() || undefined,
        nombres: nombres.trim() || undefined,
        apellidoPaterno: apellidoPaterno.trim() || undefined,
        apellidoMaterno: apellidoMaterno.trim() || undefined,
        estadoSunat: estadoSunat.trim() || undefined,
        condicionSunat: condicionSunat.trim() || undefined
      });
      onCreated(created);
      resetForm();
      onClose();
    } catch (err) {
      console.error('[ClienteQuickCreate] Error al guardar cliente:', err);
      const ve = extractValidationErrors(err);
      const first = Object.values(ve)[0];
      if (first) {
        setError(first);
      } else if (isAxiosError(err)) {
        const msg = (err.response?.data as { message?: string } | undefined)?.message;
        setError(msg ?? 'No se pudo registrar el cliente.');
      } else {
        setError('No se pudo registrar el cliente.');
      }
    } finally {
      setSaving(false);
    }
  }, [
    numeroDocumento,
    tipoDocumento,
    razonSocialNombre,
    direccion,
    nombres,
    apellidoPaterno,
    apellidoMaterno,
    estadoSunat,
    condicionSunat,
    onCreated,
    onClose,
    resetForm
  ]);

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ pb: 1 }}>Cliente nuevo (rápido)</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ mt: 0.5 }}>
          {error ? <Alert severity="error">{error}</Alert> : null}
          {info ? <Alert severity={info.includes('correctamente') ? 'success' : 'warning'}>{info}</Alert> : null}

          <Stack direction="row" spacing={1}>
            <TextField
              select
              label="Tipo doc."
              value={tipoDocumento}
              onChange={(e) => {
                setTipoDocumento(e.target.value as DocTipo);
                setNumeroDocumento('');
                setError('');
                setInfo('');
              }}
              sx={{ width: 120 }}
              disabled={saving || searchLoading}
            >
              <MenuItem value="DNI">DNI</MenuItem>
              <MenuItem value="RUC">RUC</MenuItem>
            </TextField>
            <TextField
              label="Número"
              value={numeroDocumento}
              onChange={(e) => setNumeroDocumento(e.target.value.replace(/\D/g, '').slice(0, maxLen))}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  e.preventDefault();
                  void buscarDocumento();
                }
              }}
              fullWidth
              required
              disabled={saving}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      type="button"
                      onMouseDown={(e) => e.preventDefault()}
                      onClick={() => void buscarDocumento()}
                      disabled={searchLoading || saving}
                      aria-label="Consultar RENIEC/SUNAT"
                      edge="end"
                    >
                      {searchLoading ? <CircularProgress size={18} /> : <SearchRoundedIcon />}
                    </IconButton>
                  </InputAdornment>
                )
              }}
            />
          </Stack>

          <TextField
            label={tipoDocumento === 'RUC' ? 'Razón social' : 'Nombre completo'}
            value={razonSocialNombre}
            onChange={(e) => setRazonSocialNombre(e.target.value)}
            required
            fullWidth
            disabled={saving}
          />

          <TextField
            label="Dirección (opcional)"
            value={direccion}
            onChange={(e) => setDireccion(e.target.value)}
            fullWidth
            disabled={saving}
          />
        </Stack>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleClose} disabled={saving}>
          Cancelar
        </Button>
        <Button variant="contained" onClick={() => void guardar()} disabled={saving || searchLoading}>
          {saving ? 'Guardando…' : 'Guardar y usar'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
