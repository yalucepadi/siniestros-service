package com.ylcd.service.siniestros_service.controller;

import com.ylcd.service.siniestros_service.model.request.SiniestroRequest;
import com.ylcd.service.siniestros_service.model.response.ResponseGeneralDto;
import com.ylcd.service.siniestros_service.service.impl.SiniestroServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.time.LocalDate;



import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(SiniestroController.class)
class SiniestroControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SiniestroServiceImpl service;

    private SiniestroRequest siniestroEjemplo;

    @BeforeEach
    void setUp() {
        siniestroEjemplo = new SiniestroRequest(
                "SIN001",
                "POL123",
                LocalDate.of(2025, 8, 9),
                "Colisión leve",
                "En proceso"
        );
    }

    @Test
    void registrar_DeberiaRetornar201() {
        when(service.registrarSiniestro(any(SiniestroRequest.class)))
                .thenReturn(Mono.just(siniestroEjemplo));

        webTestClient.post()
                .uri("/api/siniestros")
                .bodyValue(siniestroEjemplo)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo("201")
                .jsonPath("$.data.id").isEqualTo("SIN001")
                .jsonPath("$.data.polizaId").isEqualTo("POL123")
                .jsonPath("$.data.descripcion").isEqualTo("Colisión leve")
                .jsonPath("$.data.estado").isEqualTo("En proceso");
    }
    @Test
    void obtenerPorId_DeberiaRetornar200() {
        when(service.obtenerPorId(eq("SIN001")))
                .thenReturn(Mono.just(siniestroEjemplo));

        webTestClient.get()
                .uri("/api/siniestros/SIN001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.comment").isEqualTo("Siniestro encontrado")
                .jsonPath("$.data.id").isEqualTo("SIN001")
                .jsonPath("$.data.polizaId").isEqualTo("POL123")
                .jsonPath("$.data.descripcion").isEqualTo("Colisión leve")
                .jsonPath("$.data.estado").isEqualTo("En proceso");
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DeberiaRetornar404() {
        when(service.obtenerPorId(eq("NO_EXISTE")))
                .thenReturn(Mono.justOrEmpty(null));

        webTestClient.get()
                .uri("/api/siniestros/NO_EXISTE")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ResponseGeneralDto.class)
                .value(response -> {
                    assertThat(response.getCode()).isEqualTo("404");
                    assertThat(response.getComment()).contains("Siniestro no encontrado");
                });
    }



    @Test
    void obtenerPorPoliza_CuandoNoHayResultados_DeberiaRetornar404() {
      when(service.obtenerPorPoliza(eq("POL999")))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/siniestros")
                        .queryParam("polizaId", "POL999")
                        .build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ResponseGeneralDto.class)
                .value(response -> {
                    assertThat(response.getCode()).isEqualTo("404");
                    assertThat(response.getComment()).contains("No se encontraron siniestros para la póliza POL999");
                });
    }

    @Test
    void registrar_CuandoFallanDatos_DeberiaRetornar500() {
        SiniestroRequest siniestroInvalido = new SiniestroRequest(
                null, // ID nulo
                "POL123",
                LocalDate.of(2025, 8, 9),
                "Colisión leve",
                "En proceso"
        );

       when(service.registrarSiniestro(any(SiniestroRequest.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Error interno del servidor")));

        webTestClient.post()
                .uri("/api/siniestros")
                .bodyValue(siniestroInvalido)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseGeneralDto.class)
                .value(response -> {
                    assertThat(response.getCode()).isEqualTo("500");
                    assertThat(response.getComment()).contains("Error interno del servidor");
                });
    }

    @Test
    void registrar_CuandoPolizaNoExiste_DeberiaRetornar500() {
        when(service.registrarSiniestro(any(SiniestroRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Error interno del servidor")));

        webTestClient.post()
                .uri("/api/siniestros")
                .bodyValue(siniestroEjemplo)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseGeneralDto.class)
                .value(response -> {
                    assertThat(response.getCode()).isEqualTo("500");
                    assertThat(response.getComment()).contains("Error interno del servidor");
                });
    }

    @Test
    void obtenerPorPoliza_CuandoPolizaIdEsNulo_DeberiaRetornar400() {
        webTestClient.get()
                .uri("/api/siniestros")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseGeneralDto.class)
                .value(response -> {
                    assertThat(response.getCode()).isEqualTo("400");
                    assertThat(response.getComment()).contains("polizaId es requerido");
                });
    }
}