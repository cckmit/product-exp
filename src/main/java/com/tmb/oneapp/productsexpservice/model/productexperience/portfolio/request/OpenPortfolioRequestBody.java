package com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request;

import com.tmb.oneapp.productsexpservice.model.productexperience.client.CustomerClientModel;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.request.OccupationRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OpenPortfolioRequestBody extends CustomerClientModel {

    /* open.portfolio */
    private String suitabilityScore;

    private String portfolioType;

    private String purposeTypeCode;

    private String portfolioNickName;

    private OccupationRequest occupationRequest;
}
