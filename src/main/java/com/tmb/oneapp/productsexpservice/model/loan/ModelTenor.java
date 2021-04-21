package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ModelTenor {

    @JsonProperty("pricingModel")
    private String pricingModel;
    @JsonProperty("modelType")
    private String modelType;
    @JsonProperty("tenor")
    private String tenor;
    @JsonProperty("flagRateType")
    private String flagRateType;
    @JsonProperty("pricingTiers")
    private List<PricingTier> pricingTiers;
    @JsonProperty("principleAmount")
    private String principleAmount;
    @JsonProperty("totalInterestAmount")
    private String totalInterestAmount;
    @JsonProperty("totalAmount")
    private String totalAmount;
    @JsonProperty("firstMonthAmount")
    private String firstMonthAmount;
}