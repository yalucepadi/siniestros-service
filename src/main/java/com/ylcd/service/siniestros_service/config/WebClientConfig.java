package com.ylcd.service.siniestros_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${pacifico.polizas.base-url:http://localhost:8080}")
    private String polizasBaseUrl;

    /**
     * Bean con base-url apuntando al endpoint /polizas del microservicio polizas-service.
     * Usar @Qualifier("polizasWebClient") al inyectarlo si tienes otros WebClient.
     */
    @Bean("polizasWebClient")
    public WebClient polizasWebClient(WebClient.Builder builder) {
        // baseUrl apunta al host; añadimos /polizas para simplificar llamadas
        return builder.baseUrl(polizasBaseUrl + "/api/polizas").build();
    }
}