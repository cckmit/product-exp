package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EligibleLeadResponse {

    @JsonProperty("status")
    private Status status;
    @JsonProperty("installmentPromotions")
    private List<InstallmentPromotion> installmentPromotions;
}
