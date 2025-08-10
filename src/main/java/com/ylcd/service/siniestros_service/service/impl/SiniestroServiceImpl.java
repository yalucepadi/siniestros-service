package com.ylcd.service.siniestros_service.service.impl;

import com.ylcd.service.siniestros_service.model.request.Dto.PolizaDto;
import com.ylcd.service.siniestros_service.model.request.SiniestroRequest;
import com.ylcd.service.siniestros_service.service.SiniestroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SiniestroServiceImpl implements SiniestroService {

    private final Map<String, SiniestroRequest> siniestros = new HashMap<>();
    private WebClient polizasClient;


    public void SiniestroService(WebClient.Builder builder) {
        this.polizasClient = builder.baseUrl("http://localhost:8080/api/polizas").build();
    }

    public SiniestroServiceImpl(WebClient webClient) {
        this.polizasClient = webClient;
    }

    @Override
    public Mono<SiniestroRequest> registrarSiniestro(SiniestroRequest siniestro) {
        return polizasClient.get()
                .uri("/{id}", siniestro.polizaId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        Mono.error(new RuntimeException("Póliza no encontrada")))
                .bodyToMono(PolizaDto.class)
                .flatMap(polizaDto -> {
                    siniestros.put(siniestro.id(), siniestro);
                    return Mono.just(siniestro);
                });
    }

    @Override
    public Mono<SiniestroRequest> obtenerPorId(String id) {
        return Mono.defer(() -> {
            Optional<SiniestroRequest> optional = Optional.ofNullable(siniestros.get(id));
            return optional.map(Mono::just).orElseGet(Mono::empty);
        });
    }
    @Override
    public Flux<SiniestroRequest> obtenerPorPoliza(String polizaId) {
        return Flux.fromStream(siniestros.values().stream()
                .filter(s -> s.polizaId().equals(polizaId)));
    }
}
