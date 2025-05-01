package com.projects.marketmosaic.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseRespDTO {
    private boolean status;
    private int code;
    private String message;
    private String additionalInfo;
}
