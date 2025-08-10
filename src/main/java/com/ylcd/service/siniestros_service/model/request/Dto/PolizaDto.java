package com.ylcd.service.siniestros_service.model.request.Dto;

import java.time.LocalDate;

public record PolizaDto(
        String id,
        String tipoSeguro,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String dniCliente
) {}