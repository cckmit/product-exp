package com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenPortfolioResponseBody {

    private String crmId;

    private String clientRelationshipCode;

    private String portfolioNumber;

    private String omnibusFlag;

    private String omniNature;

    private String validationDate;
}
