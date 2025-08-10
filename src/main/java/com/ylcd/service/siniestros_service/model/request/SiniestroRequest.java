package com.ylcd.service.siniestros_service.model.request;

import java.time.LocalDate;

public record SiniestroRequest (
        String id,

        String polizaId,

        LocalDate fechaSiniestro,

        String descripcion,

        String estado ){
}
