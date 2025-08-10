package com.ylcd.service.siniestros_service.service;

import com.ylcd.service.siniestros_service.model.request.SiniestroRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SiniestroService {

    Mono<SiniestroRequest> registrarSiniestro(SiniestroRequest siniestro);
    Mono<SiniestroRequest> obtenerPorId(String id);
    Flux<SiniestroRequest> obtenerPorPoliza(String polizaId);

}
