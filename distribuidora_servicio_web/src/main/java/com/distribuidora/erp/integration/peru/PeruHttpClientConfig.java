package com.distribuidora.erp.integration.peru;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ApiPeruProperties.class)
public class PeruHttpClientConfig {

    @Bean(name = "peruApisRestClient")
    public RestClient peruApisRestClient(ApiPeruProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
