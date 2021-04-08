package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstallmentRateResponse {

    @JsonProperty("status")
    private Status status;
    @JsonProperty("installmentData")
    private InstallmentData installmentData;

}
