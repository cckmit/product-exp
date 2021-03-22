package com.tmb.oneapp.productsexpservice.model.request.fundffs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.request.alternative.AlternativeRq;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FfsRequestBody extends AlternativeRq {
    @NotNull
    private String language;
}