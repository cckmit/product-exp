package com.tmb.oneapp.productsexpservice.model.loan;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EligibleLeadResponse {

    @JsonProperty("status")
    private Status status;
    @JsonProperty("installmentPromotions")
    private List<InstallmentPromotion> installmentPromotions;
    @JsonProperty("minimumAmount")
    private String minimumAmount;
    @JsonProperty("maximumAmount")
    private String maximumAmount;
}
