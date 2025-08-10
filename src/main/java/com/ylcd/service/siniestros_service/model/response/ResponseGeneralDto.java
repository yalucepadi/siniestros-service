package com.ylcd.service.siniestros_service.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseGeneralDto {
    private String code;
    private Integer status;
    private String comment;
    private Object data;


}
