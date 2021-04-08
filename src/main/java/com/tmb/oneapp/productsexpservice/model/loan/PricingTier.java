package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)

public class PricingTier {

    @JsonProperty("tier")
    private String tier;
    @JsonProperty("minTenor")
    private String minTenor;
    @JsonProperty("maxTenor")
    private String maxTenor;
    @JsonProperty("rate")
    private String rate;
}