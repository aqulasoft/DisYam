package com.aqulasoft.disyam.models.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvocationInfoDto {

    private String hostname;
    @JsonProperty("req-id")
    private String reqId;
    @JsonProperty("exec-duration-millis")
    private String execDurationMillis;

}
