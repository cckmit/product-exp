package com.tmb.oneapp.productsexpservice.model.portfolio.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenPortfolioRequest {

    private String crmId;

    private String suitabilityScore;

    private String portfolioType;

    private String purposeTypeCode;
}
