package com.ylcd.service.siniestros_service.controller.handler;

import com.ylcd.service.siniestros_service.model.request.SiniestroRequest;

import com.ylcd.service.siniestros_service.service.impl.SiniestroServiceImpl;
import com.ylcd.service.siniestros_service.util.Constants;
import com.ylcd.service.siniestros_service.util.SiniestroAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SiniestroHandler {

    private final SiniestroServiceImpl service;


    public SiniestroHandler(SiniestroServiceImpl service) {
        this.service = service;
    }

    public Mono<ServerResponse> registrar(ServerRequest request) {
        return request.bodyToMono(SiniestroRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El cuerpo de la solicitud está vacío")))
                .flatMap(siniestro -> {
                    log.debug("Registrando siniestro: {}", siniestro);
                    return service.registrarSiniestro(siniestro)
                            .flatMap(registrado -> ServerResponse
                                    .status(HttpStatus.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(SiniestroAdapter.responseGeneral(
                                            Constants.HTTP_201, Constants.HTTP_201_code,
                                            "Siniestro registrado con éxito", registrado
                                    )));
                })
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(SiniestroAdapter.responseGeneral(
                                        Constants.HTTP_400, Constants.HTTP_400_code,
                                        "Solicitud inválida", e.getMessage()
                                ))
                )
                .onErrorResume(DecodingException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(SiniestroAdapter.responseGeneral(
                                        Constants.HTTP_400, Constants.HTTP_400_code,
                                        "Error de formato en la solicitud", e.getMessage()
                                ))
                )
                .onErrorResume(error ->
                        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(SiniestroAdapter.responseGeneral(
                                        Constants.HTTP_500, Constants.HTTP_500_code,
                                        "Error interno del servidor", error.getMessage()
                                ))
                );
    }

    public Mono<ServerResponse> obtenerPorId(ServerRequest request) {
        String id = request.pathVariable("id");
        log.debug("Buscando siniestro por ID: {}", id);

        return service.obtenerPorId(id)
                .flatMap(siniestro -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(SiniestroAdapter.responseGeneral(
                                Constants.HTTP_200, Constants.HTTP_200_code,
                                "Siniestro encontrado", siniestro
                        )))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(SiniestroAdapter.responseGeneral(
                                Constants.HTTP_404, Constants.HTTP_404_code,
                                "Siniestro no encontrado", null
                        )))
                .onErrorResume(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(SiniestroAdapter.responseGeneral(
                                Constants.HTTP_500, Constants.HTTP_500_code,
                                "Error interno del servidor", error.getMessage()
                        )));
    }

    public Mono<ServerResponse> obtenerPorPoliza(ServerRequest request) {
        String polizaId = request.queryParam("polizaId").orElse(null);
        log.debug("Listando siniestros por póliza: {}", polizaId);

        if (polizaId == null || polizaId.trim().isEmpty()) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(SiniestroAdapter.responseGeneral(
                            Constants.HTTP_400, Constants.HTTP_400_code,
                            "polizaId es requerido", null
                    ));
        }

        return service.obtenerPorPoliza(polizaId)
                .collectList()
                .flatMap(lista -> {
                    if (lista.isEmpty()) {
                        return ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(SiniestroAdapter.responseGeneral(
                                        Constants.HTTP_404, Constants.HTTP_404_code,
                                        "No se encontraron siniestros para la póliza " + polizaId,
                                        null
                                ));
                    }
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(SiniestroAdapter.responseGeneral(
                                    Constants.HTTP_200, Constants.HTTP_200_code,
                                    "Lista de siniestros encontrada", lista
                            ));
                })
                .onErrorResume(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(SiniestroAdapter.responseGeneral(
                                Constants.HTTP_500, Constants.HTTP_500_code,
                                "Error interno del servidor", error.getMessage()
                        )));
    }
}
