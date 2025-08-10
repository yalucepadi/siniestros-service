package com.ylcd.service.siniestros_service.util;


import com.ylcd.service.siniestros_service.model.response.ResponseGeneralDto;

public class SiniestroAdapter {
    public static ResponseGeneralDto responseGeneral(String code, Integer status, String message, Object data) {
        return ResponseGeneralDto.builder()
                .status(status)
                .code(code)
                .comment(message)
                .data(data)
                .build();



    }}
