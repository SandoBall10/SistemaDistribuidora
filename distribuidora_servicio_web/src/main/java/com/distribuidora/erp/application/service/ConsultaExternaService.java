package com.distribuidora.erp.application.service;

import com.distribuidora.erp.common.exception.BadRequestException;
import com.distribuidora.erp.common.exception.NotFoundException;
import com.distribuidora.erp.integration.peru.ApiPeruDocumentoJson;
import com.distribuidora.erp.integration.peru.ApiPeruProperties;
import com.distribuidora.erp.interfaces.dto.externo.ConsultaDocumentoResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ConsultaExternaService {

    private static final String PATH_DNI = "/v2/reniec/dni";
    private static final String PATH_RUC = "/v2/sunat/ruc";

    private final RestClient peruRestClient;
    private final ApiPeruProperties properties;
    private final ObjectMapper objectMapper;

    public ConsultaExternaService(
            @Qualifier("peruApisRestClient") RestClient peruRestClient,
            ApiPeruProperties properties,
            ObjectMapper objectMapper) {
        this.peruRestClient = peruRestClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public ConsultaDocumentoResponseDto consultarDocumento(String tipo, String numero) {
        if (!StringUtils.hasText(tipo)) {
            throw new BadRequestException("El parámetro tipo es obligatorio (DNI o RUC)");
        }
        String t = tipo.trim().toUpperCase();
        if (!"DNI".equals(t) && !"RUC".equals(t)) {
            throw new BadRequestException("tipo debe ser DNI o RUC");
        }

        String apiToken = properties.getToken() != null ? properties.getToken().trim() : "";
        if (!StringUtils.hasText(apiToken)) {
            throw new BadRequestException("Falta configurar api.peru.token para consultas externas");
        }

        final String numeroNorm = normalizarSoloDigitos(numero);
        if ("DNI".equals(t)) {
            if (numeroNorm.length() != 8) {
                throw new BadRequestException("El DNI debe tener 8 dígitos");
            }
        } else {
            if (numeroNorm.length() != 11) {
                throw new BadRequestException("El RUC debe tener 11 dígitos");
            }
        }

        String path = "DNI".equals(t) ? PATH_DNI : PATH_RUC;

        try {
            String body = peruRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(path).queryParam("numero", numeroNorm).build())
                    .headers(h -> {
                        h.setBearerAuth(apiToken.trim());
                        h.setAccept(List.of(MediaType.APPLICATION_JSON));
                    })
                    .retrieve()
                    .body(String.class);

            if (!StringUtils.hasText(body)) {
                throw new BadRequestException("Respuesta vacía del proveedor externo");
            }

            ApiPeruDocumentoJson json = objectMapper.readValue(body, ApiPeruDocumentoJson.class);
            rechazarSiCuerpoContieneErrorApiPeru(json);
            return "DNI".equals(t) ? mapDni(json) : mapRuc(json);
        } catch (RestClientResponseException ex) {
            throw mapPeruRestResponseToDomain(ex);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("No se pudo interpretar la respuesta del proveedor externo");
        } catch (RestClientException ex) {
            throw new BadRequestException("Fallo de conexión con el proveedor externo: " + ex.getMessage());
        }
    }

    private void rechazarSiCuerpoContieneErrorApiPeru(ApiPeruDocumentoJson json) {
        String msg = primerTexto(json.getError(), json.getMessage());
        if (StringUtils.hasText(msg)) {
            throw new BadRequestException(msg.trim());
        }
    }

    /**
     * Mapeo de códigos HTTP del proveedor a excepciones de dominio (evita propagar como 500).
     */
    private RuntimeException mapPeruRestResponseToDomain(RestClientResponseException ex) {
        if (HttpStatus.FORBIDDEN.equals(ex.getStatusCode()) || ex instanceof HttpClientErrorException.Forbidden) {
            return new BadRequestException(
                    "Error de autenticación con el servicio externo. Verifique el token.");
        }
        if (HttpStatus.NOT_FOUND.equals(ex.getStatusCode())
                || HttpStatus.UNPROCESSABLE_ENTITY.equals(ex.getStatusCode())
                || ex instanceof HttpClientErrorException.NotFound
                || ex instanceof HttpClientErrorException.UnprocessableEntity) {
            return new NotFoundException(
                    "Documento no encontrado o no válido en SUNAT/RENIEC.");
        }

        String raw = ex.getResponseBodyAsString(StandardCharsets.UTF_8);
        if (StringUtils.hasText(raw)) {
            try {
                ApiPeruDocumentoJson err = objectMapper.readValue(raw, ApiPeruDocumentoJson.class);
                String msg = primerTexto(err.getError(), err.getMessage());
                if (StringUtils.hasText(msg)) {
                    return new BadRequestException(msg.trim());
                }
            } catch (JsonProcessingException ignored) {
                // continuar con código genérico
            }
        }

        int code = ex.getStatusCode().value();
        if (code >= 400 && code < 500) {
            return new BadRequestException("Consulta rechazada por el proveedor externo (HTTP " + code + ")");
        }
        return new BadRequestException("El proveedor externo no respondió correctamente (HTTP " + code + ")");
    }

    private static String primerTexto(String a, String b) {
        if (StringUtils.hasText(a)) {
            return a.trim();
        }
        if (StringUtils.hasText(b)) {
            return b.trim();
        }
        return null;
    }

    private static String normalizarSoloDigitos(String numero) {
        if (!StringUtils.hasText(numero)) {
            return "";
        }
        return numero.replaceAll("\\D", "");
    }

    private ConsultaDocumentoResponseDto mapDni(ApiPeruDocumentoJson j) {
        ConsultaDocumentoResponseDto d = new ConsultaDocumentoResponseDto();
        d.setTipo("DNI");
        d.setNombres(trimVacios(j.getNombres()));
        d.setApellidoPaterno(trimVacios(j.getApellidoPaterno()));
        d.setApellidoMaterno(trimVacios(j.getApellidoMaterno()));
        d.setDireccion(trimVacios(j.getDireccion()));
        d.setUbigeoCodigo(trimUbigeo(j.getUbigeo()));
        d.setDepartamento(trimVacios(j.getDepartamento()));
        d.setProvincia(trimVacios(j.getProvincia()));
        d.setDistrito(trimVacios(j.getDistrito()));
        d.setEstadoSunat(trimVacios(j.getEstado()));
        d.setCondicionSunat(trimVacios(j.getCondicion()));
        return d;
    }

    private ConsultaDocumentoResponseDto mapRuc(ApiPeruDocumentoJson j) {
        ConsultaDocumentoResponseDto d = new ConsultaDocumentoResponseDto();
        d.setTipo("RUC");
        String rs = trimVacios(j.getRazonSocial());
        if (!StringUtils.hasText(rs)) {
            rs = trimVacios(j.getNombre());
        }
        d.setRazonSocial(rs);
        String nombrePlano = trimVacios(j.getNombre());
        d.setNombreComercial(nombrePlano != null && !nombrePlano.equals(rs) ? nombrePlano : null);
        d.setDireccion(trimVacios(j.getDireccion()));
        d.setUbigeoCodigo(trimUbigeo(j.getUbigeo()));
        d.setDepartamento(trimVacios(j.getDepartamento()));
        d.setProvincia(trimVacios(j.getProvincia()));
        d.setDistrito(trimVacios(j.getDistrito()));
        d.setEstadoSunat(trimVacios(j.getEstado()));
        d.setCondicionSunat(trimVacios(j.getCondicion()));
        return d;
    }

    private static String trimVacios(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        String t = s.trim();
        if (t.isEmpty() || "-".equals(t)) {
            return null;
        }
        return t;
    }

    private static String trimUbigeo(String s) {
        String t = trimVacios(s);
        if (t == null) {
            return null;
        }
        return t.length() == 6 ? t : null;
    }
}
