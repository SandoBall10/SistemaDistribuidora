import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Checkbox,
  Chip,
  CircularProgress,
  Divider,
  FormControlLabel,
  InputAdornment,
  MenuItem,
  Stack,
  TextField,
  ToggleButton,
  ToggleButtonGroup,
  Typography
} from '@mui/material';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import { isAxiosError } from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import {
  consultarDocumentoExterno,
  createProveedor,
  getProveedorById,
  getUbigeoResumen,
  listTiposDocumentoProveedor,
  listUbigeoDepartamentos,
  listUbigeoDistritos,
  listUbigeoProvincias,
  updateProveedor
} from '../services/proveedorService';
import { extractValidationErrors } from '../services/validation';
import type { TipoDocumentoCatalogo } from '../types/proveedor';
import { TIPO_PERSONA_JURIDICA, TIPO_PERSONA_NATURAL } from '../types/proveedor';

type PersonaKind = 'natural' | 'juridica';

function findTipoIdBySunat(tipos: TipoDocumentoCatalogo[], codigo: string) {
  return tipos.find((t) => t.codigoSunat?.trim() === codigo)?.id;
}

function chipSunatColor(valor: string | undefined, positivo: string) {
  if (!valor?.trim()) {
    return 'default' as const;
  }
  return valor.trim().toUpperCase() === positivo ? ('success' as const) : ('error' as const);
}

export function ProveedorFormPage() {
  const { id } = useParams<{ id: string }>();
  const isEdit = useMemo(() => Boolean(id), [id]);
  const navigate = useNavigate();
  const { session } = useAuth();

  const [personaKind, setPersonaKind] = useState<PersonaKind>('juridica');
  const [tipoDocumentoId, setTipoDocumentoId] = useState<number | ''>('');
  const [numeroDocumento, setNumeroDocumento] = useState('');
  const [nombres, setNombres] = useState('');
  const [apellidoPaterno, setApellidoPaterno] = useState('');
  const [apellidoMaterno, setApellidoMaterno] = useState('');
  const [razonSocial, setRazonSocial] = useState('');
  const [nombreComercial, setNombreComercial] = useState('');
  const [estadoSunat, setEstadoSunat] = useState('');
  const [condicionSunat, setCondicionSunat] = useState('');
  const [genero, setGenero] = useState<'M' | 'F' | ''>('');
  const [esContribuyente, setEsContribuyente] = useState(false);
  const [direccion, setDireccion] = useState('');
  const [email, setEmail] = useState('');
  const [telefono, setTelefono] = useState('');
  const [ubigeoDepartamento, setUbigeoDepartamento] = useState('');
  const [ubigeoProvincia, setUbigeoProvincia] = useState('');
  const [ubigeoDistritoCodigo, setUbigeoDistritoCodigo] = useState('');
  const [plazoCreditoDias, setPlazoCreditoDias] = useState<number | ''>('');
  const [cuentaSoles, setCuentaSoles] = useState('');

  const [tiposDocumento, setTiposDocumento] = useState<TipoDocumentoCatalogo[]>([]);
  const [departamentos, setDepartamentos] = useState<string[]>([]);
  const [provincias, setProvincias] = useState<string[]>([]);
  const [distritos, setDistritos] = useState<{ codigoUbigeo: string; nombre: string }[]>([]);

  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [searchLoading, setSearchLoading] = useState(false);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const idDni = useMemo(() => findTipoIdBySunat(tiposDocumento, '1'), [tiposDocumento]);
  const idRuc = useMemo(() => findTipoIdBySunat(tiposDocumento, '6'), [tiposDocumento]);

  const docBloqueado = personaKind === 'natural' || personaKind === 'juridica';
  const maxDocLen = personaKind === 'natural' ? 8 : 11;

  const applyUbigeoFromCode = useCallback(async (codigo: string) => {
    const c = codigo.trim();
    if (c.length !== 6) {
      return;
    }
    try {
      const u = await getUbigeoResumen(c);
      setUbigeoDepartamento(u.departamento);
      const provs = await listUbigeoProvincias(u.departamento);
      setProvincias(provs);
      setUbigeoProvincia(u.provincia);
      const dists = await listUbigeoDistritos(u.departamento, u.provincia);
      setDistritos(dists);
      setUbigeoDistritoCodigo(u.codigoUbigeo);
    } catch {
      /* Catálogo sin fila o error de red: el usuario puede elegir manualmente */
    }
  }, []);

  const syncDocTipoConPersona = useCallback(
    (kind: PersonaKind) => {
      if (kind === 'natural' && idDni) {
        setTipoDocumentoId(idDni);
      } else if (kind === 'juridica' && idRuc) {
        setTipoDocumentoId(idRuc);
      }
    },
    [idDni, idRuc]
  );

  useEffect(() => {
    async function boot() {
      setIsLoading(true);
      setError('');
      try {
        const [tipos, deps] = await Promise.all([
          listTiposDocumentoProveedor(),
          listUbigeoDepartamentos()
        ]);
        setTiposDocumento(tipos);
        setDepartamentos(deps);

        if (isEdit && id) {
          const prov = await getProveedorById(Number(id));
          const esJur = prov.tipoPersonaId === TIPO_PERSONA_JURIDICA;
          setPersonaKind(esJur ? 'juridica' : 'natural');
          setTipoDocumentoId(prov.tipoDocumentoId ?? '');
          setNumeroDocumento(prov.numeroDocumento ?? '');
          setNombres(prov.nombres ?? '');
          setApellidoPaterno(prov.apellidoPaterno ?? '');
          setApellidoMaterno(prov.apellidoMaterno ?? '');
          setRazonSocial(esJur ? (prov.razonSocial ?? prov.razonSocialNombre ?? '') : '');
          setNombreComercial(prov.nombreComercial ?? '');
          setEstadoSunat(prov.estadoSunat ?? '');
          setCondicionSunat(prov.condicionSunat ?? '');
          setGenero(prov.genero === 'F' ? 'F' : prov.genero === 'M' ? 'M' : '');
          setEsContribuyente(Boolean(prov.esContribuyente));
          setDireccion(prov.direccion ?? '');
          setEmail(prov.email ?? '');
          setTelefono(prov.telefono ?? '');
          setPlazoCreditoDias(prov.plazoCreditoDias ?? '');
          setCuentaSoles(prov.cuentaSoles ?? '');

          const cod = prov.ubigeoId?.trim();
          if (cod && cod.length === 6) {
            await applyUbigeoFromCode(cod);
          }
        } else {
          setPersonaKind('juridica');
        }
      } catch {
        setError('No se pudieron cargar los datos necesarios.');
      } finally {
        setIsLoading(false);
      }
    }
    void boot();
  }, [id, isEdit, applyUbigeoFromCode]);

  useEffect(() => {
    if (isLoading || isEdit) {
      return;
    }
    syncDocTipoConPersona(personaKind);
  }, [isLoading, isEdit, personaKind, syncDocTipoConPersona]);

  useEffect(() => {
    if (!ubigeoDepartamento) {
      setProvincias([]);
      setDistritos([]);
      return;
    }
    let cancel = false;
    (async () => {
      try {
        const p = await listUbigeoProvincias(ubigeoDepartamento);
        if (!cancel) {
          setProvincias(p);
        }
      } catch {
        if (!cancel) {
          setProvincias([]);
        }
      }
    })();
    return () => {
      cancel = true;
    };
  }, [ubigeoDepartamento]);

  useEffect(() => {
    if (!ubigeoDepartamento || !ubigeoProvincia) {
      setDistritos([]);
      return;
    }
    let cancel = false;
    (async () => {
      try {
        const d = await listUbigeoDistritos(ubigeoDepartamento, ubigeoProvincia);
        if (!cancel) {
          setDistritos(d);
        }
      } catch {
        if (!cancel) {
          setDistritos([]);
        }
      }
    })();
    return () => {
      cancel = true;
    };
  }, [ubigeoDepartamento, ubigeoProvincia]);

  const handlePersonaKindChange = (_: React.SyntheticEvent, value: PersonaKind | null) => {
    if (value === null) {
      return;
    }
    setPersonaKind(value);
    setFieldErrors({});
    setEstadoSunat('');
    setCondicionSunat('');
    if (value === 'natural') {
      setRazonSocial('');
    } else {
      setNombres('');
      setApellidoPaterno('');
      setApellidoMaterno('');
      setGenero('');
      setEsContribuyente(false);
    }
    syncDocTipoConPersona(value);
    setNumeroDocumento('');
  };

  const handleDepartamentoChange = (dep: string) => {
    setUbigeoDepartamento(dep);
    setUbigeoProvincia('');
    setUbigeoDistritoCodigo('');
  };

  const handleProvinciaChange = (prov: string) => {
    setUbigeoProvincia(prov);
    setUbigeoDistritoCodigo('');
  };

  const handleSearchDocument = async () => {
    const doc = numeroDocumento.replace(/\D/g, '');
    if (personaKind === 'juridica' && doc.length !== 11) {
      setError('Ingrese un RUC de 11 dígitos para consultar.');
      return;
    }
    if (personaKind === 'natural' && doc.length !== 8) {
      setError('Ingrese un DNI de 8 dígitos para consultar.');
      return;
    }
    setError('');
    setSearchLoading(true);
    try {
      const tipo = personaKind === 'juridica' ? ('RUC' as const) : ('DNI' as const);
      const data = await consultarDocumentoExterno(tipo, doc);
      if (personaKind === 'juridica') {
        if (data.razonSocial) {
          setRazonSocial(data.razonSocial);
        }
        if (data.direccion) {
          setDireccion(data.direccion);
        }
        if (data.nombreComercial) {
          setNombreComercial(data.nombreComercial);
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
        }
        if (data.apellidoPaterno) {
          setApellidoPaterno(data.apellidoPaterno);
        }
        if (data.apellidoMaterno) {
          setApellidoMaterno(data.apellidoMaterno);
        }
        if (data.nombreComercial) {
          setNombreComercial(data.nombreComercial);
        }
        if (data.estadoSunat) {
          setEstadoSunat(data.estadoSunat);
        }
      }
      if (data.direccion && personaKind === 'natural') {
        setDireccion(data.direccion);
      }
      if (data.ubigeoCodigo) {
        await applyUbigeoFromCode(data.ubigeoCodigo);
      }
    } catch (err) {
      if (isAxiosError(err)) {
        const body = err.response?.data as { message?: string } | undefined;
        setError(body?.message?.trim() || `Consulta rechazada (HTTP ${err.response?.status ?? ''}).`);
      } else {
        setError('No se pudo consultar el documento.');
      }
    } finally {
      setSearchLoading(false);
    }
  };

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setFieldErrors({});

    if (!session?.empresaId) {
      setError('No se pudo determinar la empresa de la sesión.');
      return;
    }
    if (tipoDocumentoId === '') {
      setFieldErrors((prev) => ({ ...prev, tipoDocumentoId: 'Seleccione un tipo de documento.' }));
      return;
    }

    const tipoPersonaId = personaKind === 'natural' ? TIPO_PERSONA_NATURAL : TIPO_PERSONA_JURIDICA;

    if (personaKind === 'natural') {
      if (!nombres.trim()) {
        setFieldErrors((prev) => ({ ...prev, nombres: 'Ingrese los nombres.' }));
        return;
      }
      if (!apellidoPaterno.trim()) {
        setFieldErrors((prev) => ({ ...prev, apellidoPaterno: 'Ingrese el apellido paterno.' }));
        return;
      }
    } else if (!razonSocial.trim()) {
      setFieldErrors((prev) => ({ ...prev, razonSocial: 'Ingrese la razón social.' }));
      return;
    }

    const plazoParsed =
      plazoCreditoDias === '' || plazoCreditoDias === null || plazoCreditoDias === undefined
        ? null
        : Number(plazoCreditoDias);
    if (plazoCreditoDias !== '' && (Number.isNaN(plazoParsed) || plazoParsed! < 0)) {
      setFieldErrors((prev) => ({ ...prev, plazoCreditoDias: 'Ingrese un número válido (≥ 0).' }));
      return;
    }

    const docNorm = numeroDocumento.replace(/\D/g, '');
    if (personaKind === 'natural' && docNorm.length !== 8) {
      setFieldErrors((prev) => ({ ...prev, numeroDocumento: 'El DNI debe tener 8 dígitos.' }));
      return;
    }
    if (personaKind === 'juridica' && docNorm.length !== 11) {
      setFieldErrors((prev) => ({ ...prev, numeroDocumento: 'El RUC debe tener 11 dígitos.' }));
      return;
    }

    const extPeru = {
      nombreComercial: nombreComercial.trim() || undefined,
      estadoSunat: estadoSunat.trim() || undefined,
      condicionSunat: condicionSunat.trim() || undefined,
      genero: personaKind === 'natural' && genero ? genero : undefined,
      esContribuyente: personaKind === 'natural' ? esContribuyente : undefined
    };

    setIsSubmitting(true);
    try {
      const base = {
        tipoPersonaId,
        tipoDocumentoId,
        numeroDocumento: docNorm,
        direccion: direccion.trim() || undefined,
        email: email.trim() || undefined,
        telefono: telefono.trim() || undefined,
        ubigeoId: ubigeoDistritoCodigo.trim() || undefined,
        plazoCreditoDias: plazoParsed,
        cuentaSoles: cuentaSoles.trim() || undefined,
        ...extPeru
      };

      if (personaKind === 'natural') {
        await (isEdit && id
          ? updateProveedor(Number(id), {
              ...base,
              nombres: nombres.trim(),
              apellidoPaterno: apellidoPaterno.trim(),
              apellidoMaterno: apellidoMaterno.trim() || undefined
            })
          : createProveedor({
              empresaId: session.empresaId,
              ...base,
              nombres: nombres.trim(),
              apellidoPaterno: apellidoPaterno.trim(),
              apellidoMaterno: apellidoMaterno.trim() || undefined
            }));
      } else {
        await (isEdit && id
          ? updateProveedor(Number(id), {
              ...base,
              razonSocial: razonSocial.trim()
            })
          : createProveedor({
              empresaId: session.empresaId,
              ...base,
              razonSocial: razonSocial.trim()
            }));
      }

      navigate('/compras/proveedores', { replace: true });
    } catch (err) {
      const mapped = extractValidationErrors(err);
      if (Object.keys(mapped).length > 0) {
        setFieldErrors(mapped);
      } else {
        setError('No se pudo guardar el proveedor. Verifica los datos e intenta nuevamente.');
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <Box>
      <Card>
        <CardContent sx={{ p: 3 }}>
          <Stack spacing={2.5} component="form" onSubmit={handleSubmit}>
            <Stack spacing={0.5}>
              <Typography variant="h5">{isEdit ? 'Editar proveedor' : 'Nuevo proveedor'}</Typography>
              <Typography variant="body2" color="text.secondary">
                Datos alineados al mercado peruano; la lupa simula SUNAT/RENIEC hasta conectar la API oficial.
              </Typography>
            </Stack>

            {error && <Alert severity="error">{error}</Alert>}

            {isLoading ? (
              <Typography variant="body2">Cargando…</Typography>
            ) : (
              <>
                <Stack spacing={1}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Tipo de persona
                  </Typography>
                  <ToggleButtonGroup
                    exclusive
                    value={personaKind}
                    onChange={handlePersonaKindChange}
                    color="primary"
                    aria-label="Tipo de persona"
                    sx={{ alignSelf: 'flex-start' }}
                  >
                    <ToggleButton value="natural">Natural</ToggleButton>
                    <ToggleButton value="juridica">Jurídica</ToggleButton>
                  </ToggleButtonGroup>
                </Stack>

                <Typography variant="subtitle2" color="text.secondary" sx={{ letterSpacing: 0.5 }}>
                  Datos de la persona
                </Typography>
                <Box
                  sx={{
                    display: 'grid',
                    gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
                    gap: 2
                  }}
                >
                  <TextField
                    select
                    label="Tipo de documento"
                    value={tipoDocumentoId}
                    onChange={(e) => setTipoDocumentoId(e.target.value === '' ? '' : Number(e.target.value))}
                    error={Boolean(fieldErrors.tipoDocumentoId)}
                    helperText={
                      fieldErrors.tipoDocumentoId ??
                      (docBloqueado ? (personaKind === 'natural' ? 'DNI (SUNAT)' : 'RUC (SUNAT)') : '')
                    }
                    required
                    disabled={docBloqueado}
                  >
                    {tiposDocumento.map((t) => (
                      <MenuItem key={t.id} value={t.id}>
                        {t.nombre}
                      </MenuItem>
                    ))}
                  </TextField>

                  <TextField
                    label="Número de documento"
                    value={numeroDocumento}
                    onChange={(e) => {
                      const v = e.target.value.replace(/\D/g, '');
                      setNumeroDocumento(v.slice(0, maxDocLen));
                    }}
                    error={Boolean(fieldErrors.numeroDocumento)}
                    helperText={fieldErrors.numeroDocumento}
                    required
                    inputProps={{ maxLength: maxDocLen, inputMode: 'numeric' }}
                    InputProps={{
                      endAdornment: (
                        <InputAdornment position="end">
                          <Button
                            type="button"
                            size="small"
                            variant="outlined"
                            sx={{ minWidth: 0, px: 1 }}
                            title="Consultar SUNAT / RENIEC (servicio propio)"
                            onClick={() => void handleSearchDocument()}
                            disabled={searchLoading}
                            aria-label="Buscar documento en SUNAT o RENIEC"
                          >
                            {searchLoading ? <CircularProgress size={18} /> : <SearchRoundedIcon fontSize="small" />}
                          </Button>
                        </InputAdornment>
                      )
                    }}
                  />

                  {personaKind === 'natural' && (
                    <>
                      <TextField
                        label="Nombre comercial"
                        value={nombreComercial}
                        onChange={(e) => setNombreComercial(e.target.value)}
                        sx={{ gridColumn: { xs: 'span 1', md: 'span 2' } }}
                      />
                      <TextField
                        select
                        label="Género"
                        value={genero}
                        onChange={(e) => setGenero(e.target.value as 'M' | 'F' | '')}
                      >
                        <MenuItem value="">
                          <em>Sin indicar</em>
                        </MenuItem>
                        <MenuItem value="M">Masculino</MenuItem>
                        <MenuItem value="F">Femenino</MenuItem>
                      </TextField>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <FormControlLabel
                          control={
                            <Checkbox
                              checked={esContribuyente}
                              onChange={(e) => setEsContribuyente(e.target.checked)}
                            />
                          }
                          label="Tiene RUC (contribuyente con documento tributario)"
                        />
                      </Box>
                      {estadoSunat ? (
                        <Stack direction="row" spacing={1} alignItems="center" sx={{ gridColumn: { xs: 'span 1', md: 'span 2' } }}>
                          <Typography variant="body2" color="text.secondary">
                            Estado SUNAT
                          </Typography>
                          <Chip size="small" label={estadoSunat} color={chipSunatColor(estadoSunat, 'ACTIVO')} />
                        </Stack>
                      ) : null}
                      <TextField
                        label="Nombres"
                        value={nombres}
                        onChange={(e) => setNombres(e.target.value)}
                        error={Boolean(fieldErrors.nombres)}
                        helperText={fieldErrors.nombres}
                        required
                        sx={{ gridColumn: { xs: 'span 1', md: 'span 2' } }}
                      />
                      <TextField
                        label="Apellido paterno"
                        value={apellidoPaterno}
                        onChange={(e) => setApellidoPaterno(e.target.value)}
                        error={Boolean(fieldErrors.apellidoPaterno)}
                        helperText={fieldErrors.apellidoPaterno}
                        required
                      />
                      <TextField
                        label="Apellido materno"
                        value={apellidoMaterno}
                        onChange={(e) => setApellidoMaterno(e.target.value)}
                        error={Boolean(fieldErrors.apellidoMaterno)}
                        helperText={fieldErrors.apellidoMaterno}
                      />
                    </>
                  )}

                  {personaKind === 'juridica' && (
                    <>
                      <TextField
                        label="Razón social"
                        value={razonSocial}
                        onChange={(e) => setRazonSocial(e.target.value)}
                        error={Boolean(fieldErrors.razonSocial)}
                        helperText={fieldErrors.razonSocial}
                        required
                        sx={{ gridColumn: { xs: 'span 1', md: 'span 2' } }}
                      />
                      <TextField
                        label="Nombre comercial"
                        value={nombreComercial}
                        onChange={(e) => setNombreComercial(e.target.value)}
                        sx={{ gridColumn: { xs: 'span 1', md: 'span 2' } }}
                      />
                      {(estadoSunat || condicionSunat) && (
                        <Stack
                          direction={{ xs: 'column', sm: 'row' }}
                          spacing={1}
                          alignItems={{ xs: 'flex-start', sm: 'center' }}
                          sx={{ gridColumn: { xs: 'span 1', md: 'span 2' } }}
                        >
                          <Typography variant="body2" color="text.secondary">
                            SUNAT
                          </Typography>
                          {estadoSunat ? (
                            <Chip size="small" label={`Estado: ${estadoSunat}`} color={chipSunatColor(estadoSunat, 'ACTIVO')} />
                          ) : null}
                          {condicionSunat ? (
                            <Chip
                              size="small"
                              label={`Condición: ${condicionSunat}`}
                              color={chipSunatColor(condicionSunat, 'HABIDO')}
                            />
                          ) : null}
                        </Stack>
                      )}
                    </>
                  )}

                  <TextField
                    label="Dirección"
                    value={direccion}
                    onChange={(e) => setDireccion(e.target.value)}
                    error={Boolean(fieldErrors.direccion)}
                    helperText={fieldErrors.direccion}
                    sx={{ gridColumn: { xs: 'span 1', md: 'span 2' } }}
                  />

                  <TextField
                    label="Email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    error={Boolean(fieldErrors.email)}
                    helperText={fieldErrors.email}
                  />

                  <TextField
                    label="Teléfono"
                    value={telefono}
                    onChange={(e) => setTelefono(e.target.value)}
                    error={Boolean(fieldErrors.telefono)}
                    helperText={fieldErrors.telefono}
                  />
                </Box>

                <Typography variant="subtitle2" color="text.secondary" sx={{ letterSpacing: 0.5 }}>
                  Ubigeo
                </Typography>
                <Box
                  sx={{
                    display: 'grid',
                    gridTemplateColumns: { xs: '1fr', md: '1fr 1fr 1fr' },
                    gap: 2
                  }}
                >
                  <TextField
                    select
                    label="Departamento"
                    value={ubigeoDepartamento}
                    onChange={(e) => handleDepartamentoChange(e.target.value)}
                  >
                    <MenuItem value="">
                      <em>Seleccione…</em>
                    </MenuItem>
                    {departamentos.map((d) => (
                      <MenuItem key={d} value={d}>
                        {d}
                      </MenuItem>
                    ))}
                  </TextField>
                  <TextField
                    select
                    label="Provincia"
                    value={ubigeoProvincia}
                    onChange={(e) => handleProvinciaChange(e.target.value)}
                    disabled={!ubigeoDepartamento}
                  >
                    <MenuItem value="">
                      <em>Seleccione…</em>
                    </MenuItem>
                    {provincias.map((p) => (
                      <MenuItem key={p} value={p}>
                        {p}
                      </MenuItem>
                    ))}
                  </TextField>
                  <TextField
                    select
                    label="Distrito"
                    value={ubigeoDistritoCodigo}
                    onChange={(e) => setUbigeoDistritoCodigo(e.target.value)}
                    disabled={!ubigeoProvincia}
                    helperText={ubigeoDistritoCodigo ? `Código: ${ubigeoDistritoCodigo}` : ''}
                  >
                    <MenuItem value="">
                      <em>Seleccione…</em>
                    </MenuItem>
                    {distritos.map((di) => (
                      <MenuItem key={di.codigoUbigeo} value={di.codigoUbigeo}>
                        {di.nombre}
                      </MenuItem>
                    ))}
                  </TextField>
                </Box>

                <Divider />

                <Typography variant="subtitle2" color="text.secondary" sx={{ letterSpacing: 0.5 }}>
                  Datos comerciales
                </Typography>
                <Box
                  sx={{
                    display: 'grid',
                    gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
                    gap: 2
                  }}
                >
                  <TextField
                    label="Plazo crédito (días)"
                    type="number"
                    inputProps={{ min: 0 }}
                    value={plazoCreditoDias}
                    onChange={(e) => {
                      const v = e.target.value;
                      setPlazoCreditoDias(v === '' ? '' : Number(v));
                    }}
                    error={Boolean(fieldErrors.plazoCreditoDias)}
                    helperText={fieldErrors.plazoCreditoDias}
                  />
                  <TextField
                    label="Cuenta soles"
                    value={cuentaSoles}
                    onChange={(e) => setCuentaSoles(e.target.value)}
                    error={Boolean(fieldErrors.cuentaSoles)}
                    helperText={fieldErrors.cuentaSoles}
                  />
                </Box>

                <Stack direction="row" spacing={1} justifyContent="flex-end" sx={{ pt: 1 }}>
                  <Button type="button" variant="outlined" onClick={() => navigate(-1)} disabled={isSubmitting}>
                    Cancelar
                  </Button>
                  <Button type="submit" variant="contained" disabled={isSubmitting}>
                    {isEdit ? 'Guardar cambios' : 'Registrar'}
                  </Button>
                </Stack>
              </>
            )}
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
