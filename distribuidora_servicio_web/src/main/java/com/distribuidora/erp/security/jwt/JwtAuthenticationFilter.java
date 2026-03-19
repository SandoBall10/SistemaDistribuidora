package com.distribuidora.erp.security.jwt;

import com.distribuidora.erp.security.config.JwtTokenType;
import com.distribuidora.erp.domain.entity.erp.Usuario;
import com.distribuidora.erp.infrastructure.repository.erp.UsuarioRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        try {
            Claims claims = jwtService.parseAndValidate(token);
            String typ = String.valueOf(claims.get("typ"));
            if (!JwtTokenType.ACCESS.name().toLowerCase().equals(typ)) {
                filterChain.doFilter(request, response);
                return;
            }

            Long usuarioId = Long.valueOf(claims.getSubject());
            Long empresaId = Long.valueOf(String.valueOf(claims.get("emp")));

            // Actualiza el "heartbeat" de actividad para que el refresh por inactividad sea un sliding window.
            // Nota: hacemos best-effort: si falla la persistencia, igual permitimos continuar con la request.
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            if (usuario == null || !usuario.isActivo() || !empresaId.equals(usuario.getEmpresaId())) {
                filterChain.doFilter(request, response);
                return;
            }

            try {
                usuario.setLastActivityAt(OffsetDateTime.now());
                usuarioRepository.save(usuario);
            } catch (Exception ex) {
                log.debug("No se pudo actualizar lastActivityAt para usuarioId={}: {}", usuarioId, ex.getMessage());
            }

            JwtPrincipal principal = new JwtPrincipal(usuarioId, empresaId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, java.util.List.of());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            log.debug("JWT inválido: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}

