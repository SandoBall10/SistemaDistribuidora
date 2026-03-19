package com.distribuidora.erp.interfaces.controller;

import com.distribuidora.erp.common.exception.BadRequestException;
import com.distribuidora.erp.domain.entity.erp.Usuario;
import com.distribuidora.erp.infrastructure.repository.erp.UsuarioRepository;
import com.distribuidora.erp.interfaces.dto.auth.DevResetPasswordRequestDto;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Profile("dev")
@RestController
@RequestMapping("/api/auth/dev")
public class DevAuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DevAuthController(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody DevResetPasswordRequestDto dto) {
        Usuario usuario = usuarioRepository.findByEmpresaIdAndNombreUsuario(dto.getEmpresaId(), dto.getNombreUsuario())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado en BD"));

        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuarioRepository.save(usuario);
    }
}

