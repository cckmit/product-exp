package com.tmb.oneapp.productsexpservice.model.request.lending;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EAppRequest {
    @NotNull
    private Long caId;
}
