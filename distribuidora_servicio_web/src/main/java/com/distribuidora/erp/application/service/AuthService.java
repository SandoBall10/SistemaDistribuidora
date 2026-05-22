package com.distribuidora.erp.application.service;

import com.distribuidora.erp.domain.entity.erp.Usuario;
import com.distribuidora.erp.infrastructure.repository.erp.UsuarioRepository;
import com.distribuidora.erp.security.config.JwtProperties;
import com.distribuidora.erp.security.jwt.JwtService;
import com.distribuidora.erp.interfaces.dto.auth.LoginRequestDto;
import com.distribuidora.erp.interfaces.dto.auth.TokenResponseDto;
import com.distribuidora.erp.interfaces.dto.auth.RefreshRequestDto;
import com.distribuidora.erp.common.exception.BadRequestException;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        JwtProperties jwtProperties) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    public TokenResponseDto login(LoginRequestDto dto) {
        Usuario usuario = usuarioRepository
                .findByEmpresaIdAndNombreUsuarioAndActivoTrue(dto.getEmpresaId(), dto.getNombreUsuario())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPasswordHash())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        OffsetDateTime now = OffsetDateTime.now();
        String refreshJti = UUID.randomUUID().toString();

        String refreshToken = jwtService.generateRefreshToken(usuario.getId(), usuario.getEmpresaId(), refreshJti);
        String refreshTokenHash = sha256Hex(refreshToken);

        usuario.setRefreshJti(refreshJti);
        usuario.setRefreshTokenHash(refreshTokenHash);
        usuario.setLastActivityAt(now);

        usuarioRepository.save(usuario);

        String accessToken = jwtService.generateAccessToken(usuario.getId(), usuario.getEmpresaId(), resolveRole(usuario));

        return toTokenResponse(accessToken, refreshToken);
    }

    public TokenResponseDto refresh(RefreshRequestDto dto) {
        String refreshToken = dto.getRefreshToken();
        Claims claims;
        try {
            claims = jwtService.parseAndValidate(refreshToken);
        } catch (Exception ex) {
            throw new BadRequestException("Refresh token inválido");
        }

        String typ = String.valueOf(claims.get("typ"));
        if (!"refresh".equalsIgnoreCase(typ)) {
            throw new BadRequestException("Refresh token inválido");
        }

        Long usuarioId = Long.valueOf(claims.getSubject());
        Long empresaId = Long.valueOf(String.valueOf(claims.get("emp")));
        String refreshJti = claims.getId();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException("Refresh token inválido"));

        if (!usuario.isActivo() || !usuario.getEmpresaId().equals(empresaId)) {
            throw new BadRequestException("Refresh token inválido");
        }

        if (usuario.getRefreshJti() == null || refreshJti == null || !refreshJti.equals(usuario.getRefreshJti())) {
            throw new BadRequestException("Refresh token inválido");
        }

        String presentedHash = sha256Hex(refreshToken);
        if (usuario.getRefreshTokenHash() == null || !usuario.getRefreshTokenHash().equals(presentedHash)) {
            throw new BadRequestException("Refresh token inválido");
        }

        OffsetDateTime lastActivityAt = usuario.getLastActivityAt();
        OffsetDateTime now = OffsetDateTime.now();
        if (lastActivityAt == null) {
            throw new BadRequestException("Sesión expirada por inactividad");
        }

        long minutesInactive = java.time.Duration.between(lastActivityAt, now).toMinutes();
        if (minutesInactive > jwtProperties.getRefreshInactivityTtlMinutes()) {
            throw new BadRequestException("Sesión expirada por inactividad");
        }

        // Rotación del refresh token: nuevo refreshJti y nuevo token.
        String newRefreshJti = UUID.randomUUID().toString();
        String newRefreshToken = jwtService.generateRefreshToken(usuario.getId(), usuario.getEmpresaId(), newRefreshJti);
        String newRefreshTokenHash = sha256Hex(newRefreshToken);

        usuario.setRefreshJti(newRefreshJti);
        usuario.setRefreshTokenHash(newRefreshTokenHash);
        usuario.setLastActivityAt(now);
        usuarioRepository.save(usuario);

        String accessToken = jwtService.generateAccessToken(usuario.getId(), usuario.getEmpresaId(), resolveRole(usuario));
        return toTokenResponse(accessToken, newRefreshToken);
    }

    private TokenResponseDto toTokenResponse(String accessToken, String refreshToken) {
        TokenResponseDto dto = new TokenResponseDto();
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(refreshToken);
        dto.setAccessExpiresInSeconds(jwtProperties.getAccessTtlMinutes() * 60);
        dto.setTokenType("Bearer");
        return dto;
    }

    private static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            // Nunca debería ocurrir en runtime normal.
            throw new IllegalStateException("No se pudo hashear token", ex);
        }
    }

    private String resolveRole(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getCodigo() == null || usuario.getRol().getCodigo().isBlank()) {
            throw new BadRequestException("El usuario no tiene rol asignado");
        }
        return usuario.getRol().getCodigo();
    }
}

