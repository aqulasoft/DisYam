package com.aqulasoft.disyam.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YaResponseDto<Result> {

    private InvocationInfoDto invocationInfo;
    private Result result;
}
