package com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EkycRiskCalculateResponse {

    @JsonProperty("max_risk")
    private String maxRisk;

    @JsonProperty("max_risk_rm")
    private String maxRiskRM;

}