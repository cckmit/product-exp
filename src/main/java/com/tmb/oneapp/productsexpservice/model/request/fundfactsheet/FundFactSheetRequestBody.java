package com.tmb.oneapp.productsexpservice.model.request.fundfactsheet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.request.AlternativeRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundFactSheetRequestBody extends AlternativeRequest {

    @NotNull
    private String crmId;

    @NotNull
    private String language;
}