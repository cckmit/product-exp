package com.tmb.oneapp.productsexpservice.model.response.suitability;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuitabilityInfo {
    private String suitabilityScore;
    private String fxFlag;
    private String createdDate;
    private String expiryDate;
    private String suitValidation;
    private String investorTypeEN;
    private String investorTypeTH;

}
