package com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountRequest {

    @NotNull
    private String portfolioNumber;

    @NotNull
    private String fundCode;

    @NotNull
    private String fundHouseCode;

    @NotNull
    private String tranType;

    @NotNull
    private String serviceType;
}
