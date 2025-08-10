package com.ylcd.service.siniestros_service.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class SiniestroException extends RuntimeException {

    private String message;

    public SiniestroException(String message) {
        super(message);
        this.message = message;
    }


}
