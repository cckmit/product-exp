package com.tmb.oneapp.productsexpservice.model.productexperience.fund.dcavalidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DcaValidationRequest {

    @NotBlank
    private String fundHouseCode;

    @NotBlank
    private String tranType;

    @NotBlank
    private String portfolioNumber;

    @NotBlank
    private String fundCode;

    @NotBlank
    private String language;

}
