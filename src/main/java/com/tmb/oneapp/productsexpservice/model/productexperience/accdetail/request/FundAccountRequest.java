package com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountRequest {

    @NotNull
    private String crmId;

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

    @NotNull
    private String getFlag;

    @NotNull
    private String portfolioList;
}
