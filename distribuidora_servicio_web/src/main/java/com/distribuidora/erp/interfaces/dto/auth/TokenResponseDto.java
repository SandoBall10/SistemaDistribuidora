package com.distribuidora.erp.interfaces.dto.auth;

public class TokenResponseDto {

    private String accessToken;
    private long accessExpiresInSeconds;
    private String refreshToken;
    private String tokenType = "Bearer";

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getAccessExpiresInSeconds() {
        return accessExpiresInSeconds;
    }

    public void setAccessExpiresInSeconds(long accessExpiresInSeconds) {
        this.accessExpiresInSeconds = accessExpiresInSeconds;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}

