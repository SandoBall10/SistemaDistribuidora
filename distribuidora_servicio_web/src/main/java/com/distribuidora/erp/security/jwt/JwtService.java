package com.distribuidora.erp.security.jwt;

import com.distribuidora.erp.security.config.JwtProperties;
import com.distribuidora.erp.security.config.JwtTokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(jwtProperties.getSecret()));
    }

    public String generateAccessToken(Long usuarioId, Long empresaId, String rol) {
        Instant now = Instant.now();
        Instant exp = now.plus(jwtProperties.getAccessTtlMinutes(), ChronoUnit.MINUTES);

        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", JwtTokenType.ACCESS.name().toLowerCase());
        claims.put("emp", empresaId);
        claims.put("rol", rol);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(usuarioId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long usuarioId, Long empresaId, String refreshJti) {
        Instant now = Instant.now();
        Instant exp = now.plus(jwtProperties.getRefreshAbsoluteTtlDays(), ChronoUnit.DAYS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", JwtTokenType.REFRESH.name().toLowerCase());
        claims.put("emp", empresaId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(usuarioId))
                .setId(refreshJti)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

