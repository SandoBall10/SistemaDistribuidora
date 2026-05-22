package com.distribuidora.erp.integration.peru;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.peru")
public class ApiPeruProperties {

    /**
     * Bearer token apis.net.pe
     */
    private String token = "";

    private String baseUrl = "https://api.apis.net.pe";

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        if (token == null) {
            this.token = "";
            return;
        }
        String t = token.strip();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            this.token = t.substring(7).strip();
            return;
        }
        this.token = t;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl != null && !baseUrl.isBlank() ? baseUrl.trim() : "https://api.apis.net.pe";
    }
}
