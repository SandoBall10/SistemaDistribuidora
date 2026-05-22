package com.distribuidora.erp.common.exception;

public class ValidationFieldError {
    private final String campo;
    private final String mensaje;

    public ValidationFieldError(String campo, String mensaje) {
        this.campo = campo;
        this.mensaje = mensaje;
    }

    public String getCampo() {
        return campo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
