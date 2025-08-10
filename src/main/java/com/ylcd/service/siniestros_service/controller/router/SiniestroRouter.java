package com.ylcd.service.siniestros_service.controller.router;

import com.ylcd.service.siniestros_service.controller.handler.SiniestroHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class SiniestroRouter {
    private final SiniestroHandler handler;

    @Bean
    public RouterFunction<ServerResponse> rutasSiniestro() {
        return RouterFunctions
                .route(RequestPredicates.POST("/api/siniestros"), handler::registrar)
                .andRoute(RequestPredicates.GET("/api/siniestros/{id}"), handler::obtenerPorId)
                .andRoute(RequestPredicates.GET("/api/siniestros"), handler::obtenerPorPoliza);
    }
}
