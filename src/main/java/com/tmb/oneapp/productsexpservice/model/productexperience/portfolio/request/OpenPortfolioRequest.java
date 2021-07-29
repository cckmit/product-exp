package com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenPortfolioRequest {

    private String suitabilityScore;

    private String portfolioType;

    private String purposeTypeCode;
}
