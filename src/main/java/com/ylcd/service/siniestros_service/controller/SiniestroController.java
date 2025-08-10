package com.ylcd.service.siniestros_service.controller;

import com.ylcd.service.siniestros_service.model.request.SiniestroRequest;
import com.ylcd.service.siniestros_service.model.response.ResponseGeneralDto;
import com.ylcd.service.siniestros_service.service.impl.SiniestroServiceImpl;
import com.ylcd.service.siniestros_service.util.SiniestroAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("api/siniestros")
public class SiniestroController {

    private final SiniestroServiceImpl service;

    public SiniestroController(SiniestroServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<ResponseGeneralDto>> registrar(@RequestBody SiniestroRequest siniestro) {
        log.debug("Registrando siniestro: {}", siniestro);

        return service.registrarSiniestro(siniestro)
                .map(registrado -> {
                    log.info("Siniestro registrado: {}", registrado);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(SiniestroAdapter.responseGeneral(
                                    "201", HttpStatus.CREATED.value(),
                                    "Siniestro registrado con éxito",
                                    registrado
                            ));
                })
                .onErrorResume(error -> {
                    log.error("Error registrando siniestro: {}", error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(SiniestroAdapter.responseGeneral(
                                    "500", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    "Error interno del servidor", error.getMessage()
                            )));
                });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponseGeneralDto>> obtenerPorId(@PathVariable String id) {
        log.debug("Buscando siniestro por ID: {}", id);

        return service.obtenerPorId(id)
                .map(siniestro -> {
                    log.info("Siniestro encontrado: {}", siniestro);
                    return ResponseEntity.ok(
                            SiniestroAdapter.responseGeneral(
                                    "200", HttpStatus.OK.value(), "Siniestro encontrado", siniestro
                            )
                    );
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(SiniestroAdapter.responseGeneral(
                                "404", HttpStatus.NOT_FOUND.value(), "Siniestro no encontrado", null
                        )))
                .onErrorResume(error -> {
                    log.error("Error al buscar siniestro {}: {}", id, error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(SiniestroAdapter.responseGeneral(
                                    "500", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    "Error interno del servidor", error.getMessage()
                            )));
                });
    }

    @GetMapping
    public Mono<ResponseEntity<ResponseGeneralDto>> obtenerPorPoliza(
            @RequestParam(required = false) String polizaId) {

        log.debug("Listando siniestros por póliza: {}", polizaId);

        // Validación manual
        if (polizaId == null || polizaId.trim().isEmpty()) {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(SiniestroAdapter.responseGeneral(
                            "400", HttpStatus.BAD_REQUEST.value(),
                            "polizaId es requerido", null
                    )));
        }

        return service.obtenerPorPoliza(polizaId)
                .collectList()
                .map(lista -> {
                    if (lista.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(SiniestroAdapter.responseGeneral(
                                        "404", HttpStatus.NOT_FOUND.value(),
                                        "No se encontraron siniestros para la póliza " + polizaId,
                                        null
                                ));
                    }
                    return ResponseEntity.ok(
                            SiniestroAdapter.responseGeneral(
                                    "200", HttpStatus.OK.value(),
                                    "Lista de siniestros encontrada", lista
                            )
                    );
                })
                .onErrorResume(error -> {
                    log.error("Error al listar siniestros para póliza {}: {}", polizaId, error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(SiniestroAdapter.responseGeneral(
                                    "500", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    "Error interno del servidor", error.getMessage()
                            )));
                });
    }
}