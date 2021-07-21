package com.tmb.oneapp.productsexpservice.model.productexperience.fund.dcavalidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DcaValidationRequest {

    private String fundHouseCode;

    private String tranType;

    private String portfolioNumber;

    private String fundCode;

    private String language;

}
